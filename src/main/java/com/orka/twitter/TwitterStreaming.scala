package com.orka.twitter

import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._

// case class to flatten tweet info
case class Tweet(user: String, tweet: String)

case class Metrics(friendsCount: Int, followersCount: Int, location: List[String], lang: List[String])

case class User(name: String, metrics: Metrics)

object TwitterStreaming {

  val logger = Logger.getLogger(getClass)

  def setupTwitter(): Unit = {

    val conf = ConfigFactory.load("twitter")
    System.setProperty("twitter4j.oauth.consumerKey", conf.getString("customerKey"))
    System.setProperty("twitter4j.oauth.consumerSecret", conf.getString("customerSecret"))
    System.setProperty("twitter4j.oauth.accessToken", conf.getString("accessToken"))
    System.setProperty("twitter4j.oauth.accessTokenSecret", conf.getString("accessTokenSecret"))
  }


  def main(args: Array[String]) {

    // Configure Twitter credentials using twitter.txt
    setupTwitter()

    val sparkConf = new SparkConf().setAppName("TweetStreaming")

    if (!sparkConf.contains("spark.master")) {
      sparkConf.setMaster("local[*]")
    }

    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create a DStream from Twitter using our streaming context and args as a filter array
    val stream = TwitterUtils.createStream(ssc, None, args)

    val users = stream.map(status => User(status.getUser.getName,
      Metrics(status.getUser.getFriendsCount,
        status.getUser.getFollowersCount,
        List(status.getUser.getLocation),
        List(status.getUser.getLang))))

    // 30 sec metrics aggregation
    val userMetrics = users
      .window(Seconds(30), Seconds(30))
      .map(u => (u.name, u.metrics))
      .reduceByKey { (m1: Metrics, m2: Metrics) =>
        Metrics(m1.friendsCount + m2.friendsCount,
          m1.followersCount + m2.followersCount,
          m1.location ::: m2.location,
          m1.lang ::: m2.lang
        )
      }
    // dump aggregation results to text files
    userMetrics.saveAsTextFiles("user_metrics")


    // map stream data to particular object stream
    val tweets = stream
      .filter(status => status.getUser.getLang.contains("ru"))  // filter Russian tweets only
      .map(status => Tweet(status.getUser.getName, status.getText.replace("\n", " ")))
    // print tweets from current stream batch
    tweets.print()

    // persists each rdd
    tweets.foreachRDD { (rdd: RDD[Tweet], time: Time) =>

      val spark = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._
      // use spark sql to retrieve records
      // place to implement ETL logic for transformation
      rdd.toDF().createOrReplaceTempView("tweets")
      //      spark.sql("select * from tweets").write.csv(s"tweets_${time.toString()}")
    }

    // create 10 seconds windowed stream
    val tweets10s = tweets.window(Seconds(10), Seconds(10))
    // persist 10 seconds batches
    //    tweets10s.saveAsTextFiles("tweets10s")

    ssc.start()
    ssc.awaitTermination()
  }


}