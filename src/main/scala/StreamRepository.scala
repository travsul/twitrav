package com.TwiTrav

import org.json4s._
import twitter4j._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

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
  private[this] def getTimeSinceStart =
    ((System.currentTimeMillis - startTime) / 1000).toInt

  def divideByTime(tweets: Int,time: Int): Int = time match {
    case 0 => tweets
    case _ => tweets / time
  }

  def getSeconds: Double = (System.currentTimeMillis - startTime) / 1000
  def getMinutes: Double = getTimeSinceStart / 60
  def getHours: Double = getTimeSinceStart / 3600

  def getSecondAvg: Int =
    divideByTime(repository.getTweets.length,getTimeSinceStart)
  def getMinuteAvg: Int =
    divideByTime(repository.getTweets.length,getMinutes.toInt)
  def getHourAvg: Int =
    divideByTime(repository.getTweets.length,getHours.toInt)

  def getUrlAvg: Int = {
    (repository.getTweets
               .count(_.hasUrl)
               .toDouble / repository.getTweets.length.toDouble * 100)
               .toInt
  }
  def getHashAvg: Int = {
    (repository.getTweets
               .count(_.hasHashtag)
               .toDouble / repository.getTweets.length.toDouble * 100)
               .toInt
  }
  def getPicAvg: Int = {
    (repository.getTweets
               .map(_.urls)
               .count { s =>
                 s.contains("instagram") || s.contains("pic.twiiter.com")
               }
               .toDouble / repository.getTweets.length.toDouble * 100)
               .toInt
  }
  def getEmojiAvg: Int = {
    (repository.getTweets
               .count(_.hasEmoji)
               .toDouble / repository.getTweets.length.toDouble * 100)
               .toInt
  }

  def getTopUrl(n: Int): Future[List[Occurrence]] = {
    repository.getDomains
              .map { domains =>
                getOccurrence(domains,n)
              }
  }

  def getTopHashtags(n: Int): Future[List[Occurrence]] = {
    repository.getHashtags
              .map { hashtags =>
                getOccurrence(hashtags,n)
              }
  }

  def getTopEmoji(n: Int): Future[List[Occurrence]] = {
    repository.getEmojis
              .map { emojis =>
                getOccurrence(emojis,n)
              }
  }

  def getOvertime: Overtime = {
    Overtime(
      seconds = getSecondAvg,
      minutes = getMinuteAvg,
      hours = getHourAvg
    )
  }

  def getAverages: Averages = {
    Averages(
      url = getUrlAvg,
      hashtag = getHashAvg,
      picture = getPicAvg,
      emoji = getEmojiAvg)
  }

  def getTopLists(n: Int = 10): Future[TopList] = {
    for {
      url <- getTopUrl(n);
      hash <- getTopHashtags(n);
      emoji <- getTopEmoji(n)
    } yield {
      TopList(
        url = url,
        hashtag = hash,
        emoji = emoji)
    }
  }

  private[this] def getOccurrence(ls: List[String], top: Int): List[Occurrence] = {
    ls.groupBy(identity)
      .mapValues(_.size)
      .toList
      .sortBy(_._2)
      .reverse
      .slice(0,top)
      .map(oc => Occurrence(oc._1,oc._2))
  }
}
