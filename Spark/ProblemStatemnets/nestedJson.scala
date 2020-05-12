package com.spark.demo.structuredstreaming

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{Column, SparkSession}
import org.apache.spark.sql.functions._

object NetstedJson {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession.builder.master("local").getOrCreate()
    import spark.implicits._
    println("actual df")
    val df = Seq(
      ("""{"a":1,"b":{"c":"H","event":"credited"}}""",
        """{"a":2,"b":{"c":"I","event":"debited"}}"""),
      ("""{"a":3,"b":{"c":"J","event":"credited"}}""",
        """{"a":2,"b":{"c":"I","event":"credited"}}""")
    ).toDF("col1","col2")

   df.filter(formFilterString(df.columns, "\"event\":\"credited\"")).show(false)
    
  }


  def formFilterString(cols: Seq[String], filterRgx: String): Column = {
    cols.map(c => column(c).contains(filterRgx)).reduce(_ && _)
  }

}
