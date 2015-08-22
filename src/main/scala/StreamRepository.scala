package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io._
import scala.concurrent._
import ExecutionContext.Implicits.global

trait TweetRepository {
  def getTweets: List[Tweet]
  def addTweet(tweet: Tweet): Future[Tweet]
  def deleteTweet(id: Long): Future[Option[Tweet]]
  def getEmojis: Future[List[String]]
  def getDomains: Future[List[String]]
  def getHashtags: Future[List[String]]
  def tweetFromStatus(tweet: Status): Tweet
}

trait TweetFunctions {
  val repository: TweetRepository

  implicit val formats = DefaultFormats

  private[this] val startTime: Long = System.currentTimeMillis
  private[this] def getTimeSinceStart: Int = ((System.currentTimeMillis - startTime) / 1000).toInt

  def divideByTime(tweets: Int,time: Int): Int = time match {
    case 0 => tweets
    case _ => (tweets / time)
  }

  def getSeconds: Double = ((System.currentTimeMillis - startTime) / 1000)
  def getSecondAvg: Int = divideByTime(repository.getTweets.length,getTimeSinceStart)
  def getMinutes: Double = (getTimeSinceStart / 60)
  def getMinuteAvg: Int = divideByTime(repository.getTweets.length,getMinutes.toInt)
  def getHours: Double = (getTimeSinceStart / 3600)
  def getHourAvg: Int = divideByTime(repository.getTweets.length,getHours.toInt)
  def getUrlAvg: Int = {
    (repository.getTweets.filter(_.hasUrl).length.toDouble / repository.getTweets.length.toDouble * 100).toInt
  }
  def getHashAvg: Int = {
    (repository.getTweets.filter(_.hasHashtag).length.toDouble / repository.getTweets.length.toDouble * 100).toInt
  }
  def getPicAvg: Int = {
    (repository.getTweets.map(_.urls).filter(s=>s.contains("instagram") || s.contains("pic.twiiter.com")).length.toDouble / repository.getTweets.length.toDouble * 100).toInt
  }
  def getEmojiAvg: Int = {
    (repository.getTweets.filter(_.hasEmoji).length.toDouble / repository.getTweets.length.toDouble * 100).toInt
  }

  def getTopUrl(n: Int): Future[List[String]] = repository.getDomains.map(domains=>getOccurrence(domains,n).map(s=>s"${s._1} @ ${s._2} uses"))

  def getTopHashtags(n: Int): Future[List[String]] = repository.getHashtags.map(hashtags => getOccurrence(hashtags,n).map(s=>s"${s._1} @ ${s._2} uses"))

  def getTopEmoji(n: Int): Future[List[String]] = repository.getEmojis.map(emojis=>getOccurrence(emojis,n).map(s=>s"${s._1} @ ${s._2} uses"))


  private[this] def getOccurrence(ls: List[String], top: Int): List[(String,Int)] = {
    ls.groupBy(identity).mapValues(_.size).toList.sortBy(_._2).reverse.slice(0,top)
  }
}
