package com.TwiTrav

import twitter4j._

trait StreamRepository {
  private[this] var tweetStream = List[Status]()
  private[this] val startTime = System.currentTimeMillis
  private[this] def getTimeSinceStart: Int = ((System.currentTimeMillis - startTime) / 1000).toInt

  def getTweets: List[String] = tweetStream.map(_.getText)

  def addTweet(tweet: Status): Option[Status] = {
    tweetStream = tweet :: tweetStream
    Some(tweet)
  }

  def getSecondAvg: Int = getTweets.length / getTimeSinceStart
  def getMinuteAvg: Int = getTweets.length / (getTimeSinceStart / 60)
  def getHourAvg: Int = getTweets.length / (getTimeSinceStart / 360)
  def getUrlAvg: Int = gatherUrls.length / getTweets.length
  def getHashAvg: Int = gatherHashtags.length / getTweets.length

  def gatherHashtags = {
    for {
      tweet <- tweetStream
      hashTag <- tweet.getHashtagEntities
    } yield hashTag.getText
  }

  def gatherUrls = {
    for {
      tweet <- tweetStream
      url <- tweet.getURLEntities
    } yield url.getURL
  }
}
