package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._

trait TwitterConnection {
  implicit val formats = DefaultFormats

  private[this] var tweetStream = List[String]()

  def getConfig(secrets: Secrets): conf.Configuration = {
    new twitter4j.conf.ConfigurationBuilder()
                      .setOAuthConsumerKey(secrets.consumerKey)
                      .setOAuthConsumerSecret(secrets.consumerSecret)
                      .setOAuthAccessToken(secrets.accessToken)
                      .setOAuthAccessTokenSecret(secrets.accessTokenSecret)
                      .build()
  }

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) {
      tweetStream = status.getText :: tweetStream
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) = ex.printStackTrace

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) {}
  }

  def getFilteredStream(secrets: Secrets, searchQuery: Array[String]): TwitterStream = {
    val twitterStream = new TwitterStreamFactory(getConfig(secrets)).getInstance
    twitterStream.addListener(simpleStatusListener)
    twitterStream.filter(new FilterQuery().track(searchQuery))
    twitterStream
  }

  def getStream(secrets: Secrets): TwitterStream = {
    val twitterStream = new TwitterStreamFactory(getConfig(secrets)).getInstance
    twitterStream.addListener(simpleStatusListener)
    twitterStream
  }

  def closeStream(stream: TwitterStream) = {
    stream.cleanUp
    stream.shutdown
  }
}
object StatusStreamer extends TwitterConnection {
  private[this] val secrets = parse(scala.io.Source.fromFile("secrets.json").getLines.mkString).extract[Secrets]

  def main(args: Array[String]) {
    getStream(secrets).sample
  }
}
