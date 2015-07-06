package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io._

object StreamRepository {
  implicit val formats = DefaultFormats

  private[this] var tweetStream: List[Status] = List[Status]()
  private[this] val startTime = System.currentTimeMillis
  private[this] def getTimeSinceStart: Int = ((System.currentTimeMillis - startTime) / 1000).toInt
  //private[this] val emoji = parse(Source.fromFile("emoji.json").getLines.mkString).extract[List[Emoji]]

  def getTweets: List[String] = tweetStream.map(_.getText)

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

  def getTopTenUrl: List[String] = {
    runEncode(getDomains).slice(0,10).map(s=>s"${s._1} @ ${s._2} uses")
  }

  def getTopTenHashtags: List[String] = {
    runEncode(gatherHashtags).slice(0,10).map(s=>s"${s._1} @ ${s._2} uses")
  }

  /*def hasEmoji: List[Boolean] = {
    getTweets.map(_.split(" ").filter(s=>emojiUni.contains(s))).map(_.length>0)
  }*/

  def getDisplayDomains = {
    for {
      tweet <- tweetStream
      url <- tweet.getURLEntities
    } yield url.getDisplayURL.mkString
  }

  def getDomains = gatherUrls.map(_.split("/")(2))

  def gatherHashtags = {
    for {
      tweet <- tweetStream
      hashTag <- tweet.getHashtagEntities
    } yield hashTag.getText.mkString
  }

  def gatherUrls = {
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

  def runEncode(urls: List[String],acc: List[(String,Int)] = Nil): List[(String, Int)] = urls match {
    case Nil => acc.sortBy(s=>s._2).reverse
    case head :: tail => {
      val togetherList = urls.map(_.toLowerCase).filter(url => url == head.toLowerCase)
      runEncode(urls.filterNot(url=>url==head), (head,togetherList.length) :: acc)
    }
  }

  def gatherPictureUrls = {
    gatherUrls.filter(s=> s.contains("instagram") || s.contains("pic.twiiter.com"))
  }
}
