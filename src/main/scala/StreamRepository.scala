package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

object StreamRepository {
  implicit val formats = DefaultFormats

  private[this] var tweetStream: List[Status] = List[Status]()
  private[this] val startTime: Long = System.currentTimeMillis
  private[this] def getTimeSinceStart: Int = ((System.currentTimeMillis - startTime) / 1000).toInt
  private[this] val emoji: Future[List[String]] = Future { parse(Source.fromFile("emoji.json").getLines.mkString)
                                                               .extract[List[Emoji]]
                                                               .map(e=>new String(e.unified.split("-").flatMap{ codepoint =>
                                                                 Character.toChars(Integer.parseInt(codepoint, 16))
                                                                })) }

  def getTweets: List[String] = tweetStream.map(_.getText)

  def getEmojiTweets: Future[List[String]] = Future(tweetStream.map(_.getText))

  def addTweet(tweet: Status): Unit = tweetStream = tweet :: tweetStream

  def divideByTime(tweets: Int,time: Int): Int = time match {
    case 0 => tweets
    case _ => (tweets / time)
  }

  def getSeconds: Double = ((System.currentTimeMillis - startTime) / 1000)
  def getSecondAvg: Int = divideByTime(tweetStream.length,getTimeSinceStart)
  def getMinutes: Double = (getTimeSinceStart / 60)
  def getMinuteAvg: Int = divideByTime(tweetStream.length,getMinutes.toInt)
  def getHours: Double = (getTimeSinceStart / 3600)
  def getHourAvg: Int = divideByTime(tweetStream.length,getHours.toInt)
  def getUrlAvg: Int = {
    (tweetStream.map(containsUrl).filter(s=>s).length.toDouble / getTweets.length.toDouble * 100).toInt
  }
  def getHashAvg: Int = {
    (tweetStream.map(containsHashTag).filter(s=>s).length.toDouble / getTweets.length.toDouble * 100).toInt
  }
  def getPicAvg: Int = {
    (tweetStream.filter(containsPicture).length.toDouble / getTweets.length.toDouble * 100).toInt
  }
  def getEmojiAvg: Int = {
    (tweetStream.filter(containsEmoji).length.toDouble / getTweets.length.toDouble * 100).toInt
  }

  def getTopTenUrl: List[String] = getOccurrence(getDomains)map(s=>s"${s._1} @ ${s._2} uses")

  def getTopTenHashtags: List[String] = getOccurrence(gatherHashtags).map(s=>s"${s._1} @ ${s._2} uses")

  def getTopTenEmoji: Future[List[String]] = getEmoji.map(e=>getOccurrence(e).map(s=>s"${s._1} @ ${s._2} uses"))

  def getEmoji: Future[List[String]] = Future {
    for {
      tweet <- getTweets
      e <- emoji
      if (tweet.contains(e))
    } yield e
  }

  def containsEmoji(status: Status): Boolean = {
    emoji.map(s=>status.getText.contains(s)).filter(s=>s).contains(true)
  }

  def getDisplayDomains: List[String] = {
    for {
      tweet <- tweetStream
      url <- tweet.getURLEntities
    } yield url.getDisplayURL.mkString
  }

  def getDomains = gatherUrls.map(_.split("/")(2))

  def gatherHashtags: List[String] = {
    for {
      tweet <- tweetStream
      hashTag <- tweet.getHashtagEntities
    } yield hashTag.getText.mkString
  }

  def gatherUrls: List[String] = {
    for {
      tweet <- tweetStream
      url <- tweet.getURLEntities
    } yield url.getExpandedURL.mkString
  }

  def containsUrl(status: Status): Boolean = {
    status.getURLEntities.map(_.getExpandedURL.mkString).filter(_.length > 0).length > 0
  }

  def containsPicture(status: Status): Boolean = {
    status.getURLEntities
      .map(_.getExpandedURL.mkString)
      .filter(s=>s.contains("instagram") || s.contains("pic.twiiter.com"))
      .length > 0
  }
  def containsHashTag(status: Status): Boolean = {
    status.getHashtagEntities.map(_.getText.mkString).filter(_.length > 0).length > 0
  }

  def getOccurrence(urls: List[String]): List[(String,Int)] = {
    urls.groupBy(identity).mapValues(_.size).toList.sortBy(_._2).reverse.slice(0,10)
  }

  def gatherPictureUrls: List[String] = {
    gatherUrls.filter(s=> s.contains("instagram") || s.contains("pic.twiiter.com"))
  }
}
