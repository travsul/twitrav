package com.TwiTrav

import twitter4j._

object StreamRepository {
  private[this] var tweetStream: List[Status] = List[Status]()
  private[this] val startTime = System.currentTimeMillis
  private[this] def getTimeSinceStart: Int = ((System.currentTimeMillis - startTime) / 1000).toInt

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
  def getHours: Double = (getTimeSinceStart / 360)
  def getHourAvg: Int = divideByTime(tweetStream.length,getHours.toInt)
  def getUrlAvg: Int = tweetStream.map(containsUrl).filter(s=>s).length / getTweets.length
  def getHashAvg: Int = tweetStream.map(containsHashTag).filter(s=>s).length / getTweets.length

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
    } yield url.getURL.mkString
  }

  def containsUrl(status: Status): Boolean = {
    status.getURLEntities.map(_.getURL.mkString).filter(_.length > 0).length > 0
  }

  def containsHashTag(status: Status): Boolean = {
    status.getHashtagEntities.map(_.getText.mkString).filter(_.length > 0).length > 0
  }
}
