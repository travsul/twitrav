package com.TwiTrav

import twitter4j._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor

trait TwitterConnection extends StreamRepository {
  implicit val formats = DefaultFormats

  def getConfig(secrets: Secrets): conf.Configuration = {
    new twitter4j.conf.ConfigurationBuilder()
                      .setOAuthConsumerKey(secrets.consumerKey)
                      .setOAuthConsumerSecret(secrets.consumerSecret)
                      .setOAuthAccessToken(secrets.accessToken)
                      .setOAuthAccessTokenSecret(secrets.accessTokenSecret)
                      .build()
  }

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) = addTweet(status)

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
object StatusStreamer extends TwitterConnection with StreamRepository {
  private[this] val secrets = parse(scala.io.Source.fromFile("secrets.json").getLines.mkString).extract[Secrets]
  val system = ActorSystem("TweetSystem")

  def main(args: Array[String]) {
    val actor = system.actorOf(Props[StreamActor],name = "streamactor")
    actor ! StartStream(secrets)
  }
}
