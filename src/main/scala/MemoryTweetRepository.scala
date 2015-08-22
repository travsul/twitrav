package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io._
import scala.concurrent._
import ExecutionContext.Implicits.global

class MemoryTweetRepository extends TweetRepository {
  implicit val formats = DefaultFormats

  private[this] var tweetStream: List[Tweet] = List[Tweet]()
  private[this] val emoji: List[String] = parse(Source.fromFile("emoji.json").getLines.mkString)
                                                               .extract[List[Emoji]]
                                                               .map(e=>new String(e.unified.split("-").flatMap{ codepoint =>
                                                                 Character.toChars(Integer.parseInt(codepoint, 16))
                                                                }))

  private[this] var emojisInTweets: List[String] = List[String]()

  def getTweets: List[Tweet] = tweetStream

  def tweetFromStatus(tweet: Status): Tweet = {
    val id = tweet.getId
    val hasUrl = containsUrl(tweet)
    val hasHashtag = containsHashtag(tweet)
    val hasEmoji = containsEmoji(tweet)
    val urls = tweet.getURLEntities.map(_.getExpandedURL.mkString).toList
    val hashtags = tweet.getHashtagEntities.map(_.getText.mkString).toList
    val emojis = emojisContained(tweet.getText)
    Tweet(id,tweet.getText,hasUrl,hasEmoji,hasHashtag,urls,emojis,hashtags)
  }

  def addTweet(tweet: Tweet): Future[Tweet] = Future {
    tweetStream = tweet :: tweetStream
    tweet
  }

  private[this] def emojisContained(text: String): List[String] = {
    emoji.filter(e=>text.contains(e))
  }

  def deleteTweet(id: Long): Future[Option[Tweet]] = Future {
    val maybeTweet = tweetStream.find(_.id == id)
    tweetStream = tweetStream.filterNot(_.id == id)
    maybeTweet
  }

  def getEmojis: Future[List[String]] = Future(getTweets.flatMap(_.emojis))

  def getHashtags: Future[List[String]] = Future(getTweets.flatMap(_.hashtags))

  def getUrls: Future[List[String]] = Future(getTweets.flatMap(_.urls))

  private[this] def containsEmoji(status: Status): Boolean = {
    emoji.foreach(s=>if (status.getText.contains(s)) return true)
    return false
  }

  def getDomains = Future {
    for {
      tweet <- getTweets
      url <- tweet.urls
    } yield (url.split("/")(2))
  }

  private[this] def containsUrl(status: Status): Boolean = {
    status.getURLEntities
      .map(_.getExpandedURL.mkString)
      .filter(_.length > 0)
      .length > 0
  }

  private[this] def containsHashtag(status: Status): Boolean = {
    status.getHashtagEntities
      .map(_.getText)
      .filter(_.length > 0)
      .length > 0
  }

  private[this] def containsPicture(status: Status): Boolean = {
    status.getURLEntities
      .map(_.getExpandedURL.mkString)
      .filter(s=>s.contains("instagram") || s.contains("pic.twiiter.com"))
      .length > 0
  }
}
