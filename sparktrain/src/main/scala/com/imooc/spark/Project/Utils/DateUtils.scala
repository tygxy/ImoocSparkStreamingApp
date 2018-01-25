package com.imooc.spark.Project.Utils

import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

/**
  * Created by guoxingyu on 2018/1/24.
  * 日期时间工具类
  */
object DateUtils {
  val YYYYMMDDHHMMSS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
  val TARGE_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss")

  def getTime(time : String) = {
    YYYYMMDDHHMMSS_FORMAT.parse(time).getTime
  }

  def parseToMinute(time : String) = {
    TARGE_FORMAT.format(new Date(getTime(time)))
  }
}
