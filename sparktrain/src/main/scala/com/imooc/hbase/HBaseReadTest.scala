package com.imooc.hbase

import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client.HBaseAdmin
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.{SparkConf, SparkContext}




/**
  * Created by guoxingyu on 2018/7/23.
  * Spark从HBase中读数据
  */
object HBaseReadTest {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("HBaseTest").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)

    val tableName = "imooc_course_clickcount"

    // 设置zookeeper集群地址
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum","localhost")
    conf.set("hbase.zookeeper.property.clientPort","2181")
    conf.set(TableInputFormat.INPUT_TABLE,tableName)

    // 如果表不存在则创建表
    val admin = new HBaseAdmin(conf)
    if (!admin.isTableAvailable(tableName)) {
      val tableDesc = new HTableDescriptor(TableName.valueOf(tableName))
      admin.createTable(tableDesc)
    }

    // 读取数据并转化成RDD
    val hBaseRDD = sc.newAPIHadoopRDD(conf,classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])


//    val count = hBaseRDD.count()
//    println(count)

    // 读取值
    hBaseRDD.foreach{case (_, result) => {
      // 获取行键
      val key = Bytes.toString(result.getRow)
      // 通过列族和列名获取列
      val clict_count = Bytes.toString(result.getValue("info".getBytes,"click_count".getBytes))
      println("Row key:"+key+" clict_count:"+clict_count)
      }
    }

    sc.stop()
    admin.close()



  }

}
