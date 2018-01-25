package com.imooc.spark.Project.domain

/**
  * Created by guoxingyu on 2018/1/25.
  * 实战课程点击数实体类
  * @param day_course ,对应Hbase中的rowKey,20171111_123
  * @param click_count,对应的20171111_123的访问总数
  */
case class CourseClickCount (day_course:String, click_count:Long)
