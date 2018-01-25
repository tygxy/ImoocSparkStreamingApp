package com.imooc.spark.Project.DAO

import com.imooc.spark.Project.domain.{CourseSearchClick}
import com.imooc.spark.project.Utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * Created by guoxingyu on 2018/1/25.
  * 从搜索引擎过来的实战课程点击数数据访问层
  */
object CourseSearchClickCountDAO {
  val tableName = "imooc_course_search_clickcount"
  val cf = "info"
  val qualifer = "click_count"

  /**
    * 保存数据到HBase
    * @param list
    */
  def save (list : ListBuffer[CourseSearchClick]) : Unit = {
    val table = HBaseUtils.getInstance().getTable(tableName)
    for (ele <- list) {
      table.incrementColumnValue(Bytes.toBytes(ele.day_search_course),Bytes.toBytes(cf),Bytes.toBytes(qualifer),ele.click_count)
    }
  }

  /**
    * 根据rowkey查询值
    * @param day_search_course
    * @return
    */
  def count(day_search_course: String):Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)
    val get = new Get(Bytes.toBytes(day_search_course))
    val value = table.get(get).getValue(cf.getBytes,qualifer.getBytes)
    if (value == null) {
      0L
    } else {
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[CourseSearchClick]
    list.append(CourseSearchClick("20171111_www.baidu.com_8",8))
    list.append(CourseSearchClick("20171111_cn.bing.com_9",10))
    save(list)

//    println(count("20171111_www.baidu.com_8") + ":" + count("20171111_cn.bing.com_9"))
  }

}
