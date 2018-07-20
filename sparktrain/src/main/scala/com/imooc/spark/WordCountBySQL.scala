package com.imooc.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/7/20.
  * spark streaming 转换成DF，通过SQL命令操作
  */
object WordCountBySQL {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("ForeachRDDApp")
    val ssc = new StreamingContext(sparkConf,Seconds(5))

    val lines = ssc.socketTextStream("localhost",6789)
    val result = lines.flatMap(_.split(" "))

    result.foreachRDD({ rdd =>
      val spark = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._

      val wordsDataFrame = rdd.toDF("word")

      wordsDataFrame.createOrReplaceTempView("words")

      val wordCountsDataFrame = spark.sql("select word ,count(1) as total from words group by word")

      wordCountsDataFrame.show()
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
