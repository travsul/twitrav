package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io._
import scala.concurrent._
import ExecutionContext.Implicits.global

trait TweetRepository {
  def getTweets: List[Tweet]
  def addTweet(tweet: Status): Future[Tweet]
  def deleteTweet(notice: StatusDeletionNotice): Future[Option[Tweet]]
  def getEmojis: Future[List[String]]
  def getDomains: Future[List[String]]
  def getHashtags: Future[List[String]]
}

object MemoryTweetRepository extends TweetRepository {
  implicit val formats = DefaultFormats

  private[this] var tweetStream: List[Tweet] = List[Tweet]()
  private[this] val emoji: List[String] = parse(Source.fromFile("emoji.json").getLines.mkString)
                                                               .extract[List[Emoji]]
                                                               .map(e=>new String(e.unified.split("-").flatMap{ codepoint =>
                                                                 Character.toChars(Integer.parseInt(codepoint, 16))
                                                                }))

  private[this] var emojisInTweets: List[String] = List[String]()

  def getTweets: List[Tweet] = tweetStream

  def addTweet(tweet: Status): Future[Tweet] = Future {
    val id = tweet.getId
    val hasUrl = containsUrl(tweet)
    val hasHashtag = containsHashtag(tweet)
    val hasEmoji = containsEmoji(tweet)
    val urls = tweet.getURLEntities.map(_.getExpandedURL.mkString).toList
    val hashtags = tweet.getHashtagEntities.map(_.getText.mkString).toList
    val emojis = tweet.getText.toList.map(_.toString).filter(isEmoji)
    val newTweet = Tweet(id,tweet.getText,hasUrl,hasEmoji,hasHashtag,urls,emojis,hashtags)
    tweetStream = newTweet :: tweetStream
    newTweet
  }

  def isEmoji(e: String): Boolean = emoji.contains(e)

  def deleteTweet(notice: StatusDeletionNotice): Future[Option[Tweet]] = Future {
    val maybeTweet = tweetStream.find(_.id == notice.getStatusId)
    tweetStream = tweetStream.filterNot(_.id == notice.getStatusId)
    maybeTweet
  }

  def getEmojis: Future[List[String]] = Future(getTweets.flatMap(_.emojis))

  def getHashtags: Future[List[String]] = Future(getTweets.flatMap(_.hashtags))

  def getUrls: Future[List[String]] = Future(getTweets.flatMap(_.urls))

  def containsEmoji(status: Status): Boolean = {
    emoji.foreach(s=>if (status.getText.contains(s)) return true)
    return false
  }

  def getDomains = Future {
    for {
      tweet <- getTweets
      url <- tweet.urls
    } yield (url.split("/")(2))
  }

  def containsUrl(status: Status): Boolean = {
    status.getURLEntities
      .map(_.getExpandedURL.mkString)
      .filter(_.length > 0)
      .length > 0
  }

  def containsHashtag(status: Status): Boolean = {
    status.getHashtagEntities
      .map(_.getText)
      .filter(_.length > 0)
      .length > 0
  }

  def containsPicture(status: Status): Boolean = {
    status.getURLEntities
      .map(_.getExpandedURL.mkString)
      .filter(s=>s.contains("instagram") || s.contains("pic.twiiter.com"))
      .length > 0
  }
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

  def getTopTenUrl: Future[List[String]] = repository.getDomains.map(domains=>getOccurrence(domains).map(s=>s"${s._1} @ ${s._2} uses"))

  def getTopTenHashtags: Future[List[String]] = repository.getHashtags.map(hashtags => getOccurrence(hashtags).map(s=>s"${s._1} @ ${s._2} uses"))

  def getTopTenEmoji: Future[List[String]] = repository.getEmojis.map(emojis=>getOccurrence(emojis).map(s=>s"${s._1} @ ${s._2} uses"))

  
  def getOccurrence(ls: List[String]): List[(String,Int)] = {
    ls.groupBy(identity).mapValues(_.size).toList.sortBy(_._2).reverse.slice(0,10)
  }
}
