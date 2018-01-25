package com.imooc.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/1/23.
  * Spark Streaming对接Kafka方式一：基于Receiver
  */
object KafKaReceiverWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      System.err.println("Usage:KafKaReceiverWordCount <zkQuorum> <group> <topics> <numThreads>")
      System.exit(1)
    }
    val sparkConf = new SparkConf().setAppName("KafKaReceiverWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf,Seconds(5));
    val Array(zkQuorum,group,topics,numThreads) = args

    val topicMap = topics.split(",").map((_,numThreads.toInt)).toMap

    val messages = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)

    messages.map(_._2).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).print()


    ssc.start()
    ssc.awaitTermination()
  }

}
