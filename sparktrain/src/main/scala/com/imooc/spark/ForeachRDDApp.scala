package com.imooc.spark

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/1/22.
  * 统计词频,保存到MySQL中
  * 使用窗口函数window统计词频
  */
object ForeachRDDApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("ForeachRDDApp")
    val ssc = new StreamingContext(sparkConf,Seconds(5))

    val lines = ssc.socketTextStream("localhost",6789)
    val result = lines.flatMap(_.split(" ")).map((_,1))


    // 窗口函数的使用
//    val windowedWordCount = result.reduceByKeyAndWindow((a:Int,b:Int) => (a+b),Seconds(30),Seconds(10))

    result.foreachRDD(RDD => {
      RDD.foreachPartition(partitionofRecords => {
        val connection = createConnection()
        partitionofRecords.foreach(record => {
          val sql = "insert into wordcount(word, wordcount) values('" + record._1 + "'," + record._2 + ")"
          connection.createStatement().execute(sql)
        })
        connection.close()
      })
    })

//    windowedWordCount.print()
    result.print()


    ssc.start()
    ssc.awaitTermination()
    }

  /**
    * 获取MySQL的连接
    * @return
    */
  def createConnection() = {
    DriverManager.getConnection("jdbc:mysql://localhost:3306/imooc_project?user=root&password=302313")
  }
}
