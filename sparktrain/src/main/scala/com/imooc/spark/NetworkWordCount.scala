package com.imooc.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/1/21.
  * Spark Streaming处理Socket数据
  *
  * 测试 nc -lk 6789
  */
object NetworkWordCount {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(sparkConf,Seconds(5))

    val lines = ssc.socketTextStream("localhost",6789)
    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
