package com.imooc.spark.Project.Spark

import com.imooc.spark.Project.DAO.{CourseClickCountDAO, CourseSearchClickCountDAO}
import com.imooc.spark.Project.Utils.DateUtils
import com.imooc.spark.Project.domain.{ClickLog, CourseClickCount, CourseSearchClick}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

/**
  * Created by guoxingyu on 2018/1/24.
  * 使用Spark Streaming处理Kafka过来的数据
  */
object ImoocStatStreamingApp {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      // args = localhost:2181 test streamingtopic 1
      System.err.println("Usage:KafKaReceiverWordCount <zkQuorum> <group> <topics> <numThreads>")
      System.exit(1)
    }
    val sparkConf = new SparkConf().setAppName("ImoocStatStreamingApp").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf,Seconds(10));
    val Array(zkQuorum,group,topics,numThreads) = args
    val topicMap = topics.split(",").map((_,numThreads.toInt)).toMap

    val messages = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)
    // 测试数据接收
//    messages.map(_._2).count().print()


    // 数据清洗,格式(ip,time,courseId,statusCode,referer)
    val logs = messages.map(_._2)
    var courseId = 0
    val cleanLogs = logs.map(line => {
      val infos = line.split("\t")
      val url = infos(2).split(" ")(1)
      if (url.startsWith("/class")) {
        val courseIdHtml = url.split("/")(2)
        courseId = courseIdHtml.substring(0,courseIdHtml.lastIndexOf(".")).toInt
      }
      ClickLog(infos(0),DateUtils.parseToMinute(infos(1)),courseId,infos(3).toInt,infos(4))
    }).filter(ClickLog => ClickLog.courseId != 0).cache()

    // 功能一：统计今天到现在为止实战课程的访问量
    cleanLogs.map(x=> {
      (x.time.substring(0,8) + "_ " + x.courseId,1)
    }).reduceByKey(_ + _).foreachRDD(rdd => {
      rdd.foreachPartition(partitionRecords => {
        val list = new ListBuffer[CourseClickCount]
        partitionRecords.foreach(pair => {
          list.append(CourseClickCount(pair._1,pair._2))
        })
        CourseClickCountDAO.save(list)
      })
    })

    // 功能二：统计从搜索引擎过来的今天到现在为止实战课程的访问量
    cleanLogs.map(x => {
      val referer = x.referer.replaceAll("//","/")
      val splits = referer.split("/")
      var host = ""
      if (splits.length > 2) {
        host = splits(1)
      }
      (host,x.courseId,x.time)
    }).filter(_._1 != "").map(x => {
      (x._3.substring(0,8) + "_" + x._1 + "_" + x._2,1)
    }).reduceByKey(_ + _).foreachRDD(rdd => {
      rdd.foreachPartition(partitionRecords => {
        val list = new ListBuffer[CourseSearchClick]
        partitionRecords.foreach(pair => {
          list.append(CourseSearchClick(pair._1,pair._2))
        })
        CourseSearchClickCountDAO.save(list)
      })
    })








    ssc.start()
    ssc.awaitTermination()

  }

}
