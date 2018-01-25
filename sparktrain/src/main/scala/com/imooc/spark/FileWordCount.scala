package com.imooc.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/1/21.
  * Spark Streaming处理文件系统数据
  */

object FileWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    val ssc = new StreamingContext(sparkConf,Seconds(5))

    val lines = ssc.textFileStream("file:///Users/guoxingyu/data/imooc/")
    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
