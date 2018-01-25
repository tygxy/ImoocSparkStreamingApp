package com.imooc.spark


import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/1/23.
  * Spark Streaming对接Kafka方式二：Direct方式
  * 版本需要匹配，kafka的版本是0.8.2.2
  */
object KafKaDirectWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.println("Usage:KafKaDirectWordCount <brokers> <topics>")
      System.exit(1)
    }
    val sparkConf = new SparkConf().setAppName("KafKaDirectWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf,Seconds(5));
    val Array(brokers,topics) = args


    val kafkaParams = Map[String,String]("metadata.broker.list" -> brokers)
    val topicsSet = topics.split(",").toSet

    val messages = KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](ssc,kafkaParams,topicsSet)

    messages.map(_._2).flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).print()


    ssc.start()
    ssc.awaitTermination()
  }

}
