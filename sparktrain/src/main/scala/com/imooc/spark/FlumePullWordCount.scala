package com.imooc.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by guoxingyu on 2018/1/22.
  * spark Streaming整合Flume的pull方式，本地测试
  * 测试方式：1.首先启动flume agent simple-agent(file:///$FLUME_HOME/conf/flume_pull_streaming.conf)
  *          2.启动spark streaming
  *          3.通过启动Telnet hostname port，观察控制台输出
  *
  *          flume-ng agent --name simple-agent --conf $FLUME_HOME/conf  \
  *                         --conf-file $FLUME_HOME/conf/flume_push_streaming.conf  \
  *                         -Dflume.root.logger=INFO,console
  *
  *
  *
  */
object FlumePullWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.err.print("Usage:FlumePushWordCount <hostname> <port>")
      System.exit(1)
    }

    val sparkConf = new SparkConf().setAppName("FlumePullWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf,Seconds(5))

    val Array(hostname,port) = args
    val flumeStream = FlumeUtils.createPollingStream(ssc,hostname,port.toInt)

    flumeStream.map(x=> new String(x.event.getBody.array()).trim)
      .flatMap(_.split(" ")).map((_,1)).reduceByKey(_ + _).print()


    ssc.start()
    ssc.awaitTermination()
  }

}
