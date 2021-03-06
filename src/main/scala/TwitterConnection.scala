package com.TwiTrav

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.json4s._
import org.json4s.jackson.JsonMethods._
import spray.can.Http
import twitter4j._
import twitter4j.conf._

import scala.concurrent.duration._
import scala.io._

trait TwitterConnection extends TweetFunctions {
  val repository = RepositoryConnection.repository

  implicit val system = ActorSystem("TweetSystem")

  val actor = system.actorOf(Props[StreamActor], name = "streamactor")
  val log = Logging(system, actor)

  def getConfig(secrets: Secrets): Configuration =
    new ConfigurationBuilder().setOAuthConsumerKey(secrets.consumerKey)
                              .setOAuthConsumerSecret(secrets.consumerSecret)
                              .setOAuthAccessToken(secrets.accessToken)
                              .setOAuthAccessTokenSecret(secrets.accessTokenSecret)
                              .build()

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) = {
      actor ! AddTweet(repository.tweetFromStatus(status))
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {
      actor ! DeleteTweet(statusDeletionNotice.getStatusId)
    }

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) = ex.printStackTrace()

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) = log.warning(warning.getMessage)
  }

  def extractSecrets(maybeJson: Option[String]): Option[Secrets] = {
    maybeJson.map(fileName => parse(Source.fromFile(fileName).getLines.mkString).extract[Secrets])
  }

  def getStream(secrets: Secrets): TwitterStream = {
    val twitterStream = new TwitterStreamFactory(getConfig(secrets)).getInstance
    twitterStream.addListener(simpleStatusListener)
    twitterStream
  }

  def closeStream(stream: TwitterStream) = {
    stream.cleanUp()
    stream.shutdown()
  }
}
object StatusStreamer extends App with TwitterConnection {
  private[this] val maybeSecrets = extractSecrets(Option("secrets.json"))
  maybeSecrets.foreach(secrets => getStream(secrets).sample())

  implicit val timeout = Timeout(5.seconds)

  val routeActor = system.actorOf(Props[RouteActor], "stream-service")

  IO(Http) ? Http.Bind(routeActor, interface = "localhost", port = 8080)
}
