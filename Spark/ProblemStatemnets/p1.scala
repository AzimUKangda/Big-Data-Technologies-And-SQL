/*
Given a time series data which is a clickstream of user activity is stored in hive, ask is to enrich the data with session id.
Session Definition:
•	Session expires after inactivity of 30 mins, because of inactivity no clickstream record will be generated. 
•	Session remains active for a total duration of 2 hours 
Steps:
•	Load Data in Hive Table.
•	Read the data from hive, use spark batch (Scala) to do the computation. 
•	Save the results in parquet with enriched data.
Note: Please do not use direct spark-sql.

timestamp	userid
2018-01-01T11:00:00Z	u1
2018-01-01T12:00:00Z	u1
2018-01-01T11:00:00Z	u2
2018-01-02T11:00:00Z	u2
2018-01-01T12:15:00Z	u1

                                                                               
                                                                     QUESTION 3
In addition to the problem statement given in question 2 assume below scenario as well and design hive table based on it:
•	Get Number of sessions generated in a day.
•	Total time spent by a user in a day 
•	Total time spent by a user over a month.
Here are the guidelines and instructions for the solution of above queries:
•	Design hive table
•	Write the script to create the table
•	Load data into table
•	Write all the queries in spark-sql
•	Think in the direction of using partitioning, bucketing, etc. 

 */

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.{UserDefinedFunction, Window}

object UserActivity {

  val timeFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
  val secondsForthirtyMins = 1800
  val secondsForTwoHrs = 7200

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._
    val data = Seq(
      ("2018-01-01T11:00:00Z", "u1"),
      ("2018-01-01T12:00:00Z", "u1"),
      ("2018-01-01T11:00:00Z", "u2"),
      ("2018-01-02T11:00:00Z", "u2"),
      ("2018-01-01T12:15:00Z", "u1")
    ).toDF("timestamp", "userId")

    data.show(false)

    val window = Window.partitionBy("userID").orderBy("timestamp")

    val timeDiffDf = data
      .withColumn("timeDiff",
                  unix_timestamp($"timestamp", timeFormat) - unix_timestamp(
                    lag($"timestamp", 1, 0).over(window),
                    timeFormat))
      .withColumn(
        "timeDiff",
        when(row_number()
               .over(window) === 1 || $"timeDiff" >= secondsForthirtyMins,
             0L).otherwise($"timeDiff"))
    timeDiffDf.show(false)
    timeDiffDf
      .groupBy("userID")
      .agg(collect_list($"timestamp").as("time_list"),
           collect_list($"timeDiff").as("timeDiff_list"))
      .withColumn(
        "sessionList",
        explode(getSessionIdList($"userID", $"time_list", $"timeDiff_list")))
      .select($"userID",
              $"sessionList._1".as("timestamp"),
              $"sessionList._2".as("sessionId"))
      .show(false)

    timeDiffDf
      .withColumn("date", date_format($"timestamp", "YYYY-MM-dd"))
      .groupBy($"userID", $"date")
      .agg(sum($"timeDiff")/60)
      .show(false)
  }

  def getSessionIdList: UserDefinedFunction = udf {
    (userId: String, clickList: Seq[String], tsList: Seq[Long]) =>
      def getSessionID(n: Long) = s"$userId-Session$n"

      val sessions = tsList
        .foldLeft(List[String](), 0L, 0L) {
          case ((l, j, k), i) =>
            if (i == 0) (getSessionID(k + 1) :: l, 0L, k + 1)
            else if (j + i < secondsForTwoHrs) (getSessionID(k) :: l, j + i, k)
            else (getSessionID(k + 1) :: l, 0L, k + 1)
        }
        ._1
        .reverse

      clickList zip sessions

  }

}
