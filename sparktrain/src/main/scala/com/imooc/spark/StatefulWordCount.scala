package com.imooc.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/1/22.
  * 统计截止目前为止的词频
  */
object StatefulWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("StatefulWordCount")
    val ssc = new StreamingContext(sparkConf,Seconds(5))
    // 如果使用带状态的算子，必须设置Checkpoint
    // 在生产环境中，Checkpoint保存在HDFS中
    ssc.checkpoint(".")

    val lines = ssc.socketTextStream("localhost",6789)
    val result = lines.flatMap(_.split(" ")).map((_,1))
    val state = result.updateStateByKey(updateFunction _)
    state.print()


    ssc.start()
    ssc.awaitTermination()
    }

  /**
    * 把当前的数据去更新已有的或者老的数据
    * @param currentValues
    * @param preValues
    * @return
    */
  def updateFunction(currentValues: Seq[Int], preValues: Option[Int]): Option[Int] = {
    val current = currentValues.sum
    val pre = preValues.getOrElse(0)

    Some(current + pre)

  }

}
