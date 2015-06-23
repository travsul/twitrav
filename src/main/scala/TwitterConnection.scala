package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._

trait TwitterConnection {
  implicit val formats = DefaultFormats

  private[this] val secrets = parse(scala.io.Source.fromFile("secrets.json").getLines.mkString).extract[Secrets]
  private[this] val config = getConfig(secrets)
  private[this] val streamFactory = new TwitterStreamFactory(config)
  
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
      println(status.getText)
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) = ex.printStackTrace

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) {}
  }

  def getFilteredStream(searchQuery: Array[String]): TwitterStream = {
    val twitterStream = streamFactory.getInstance
    twitterStream.addListener(simpleStatusListener)
    twitterStream.filter(new FilterQuery().track(searchQuery))
    twitterStream
  }

  def getStream: TwitterStream = {
    val twitterStream = streamFactory.getInstance
    twitterStream.addListener(simpleStatusListener)
    twitterStream
  }

  def closeStream(stream: TwitterStream) = {
    stream.cleanUp
    stream.shutdown
  }
}
object StatusStreamer extends TwitterConnection {
  def main(args: Array[String]) {
    getStream.sample
  }
}
