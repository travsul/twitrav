package com.TwiTrav

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import spray.routing._
import spray.http._
import MediaTypes._
import scala.concurrent._
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global

class StreamActor extends Actor with StreamService {
  def actorRefFactory = context

  def receive = {
    case AddTweet(tweet) => repository.addTweet(tweet)
    case DeleteTweet(notice) => repository.deleteTweet(notice)
  }
}

class RouteActor extends Actor with StreamService {
  def actorRefFactory = context

  def receive = runRoute(streamRoute)
}

trait StreamService extends HttpService with TweetFunctions {
  val repository = RepositoryConnection.repository

  val streamRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
            <body>
            <ul>
            <li><a href="/totaltweets">Total tweets</a></li>
            <li><a href="/timelyData">Time data</a></li>
            <li><a href="/hourData">Hourly data</a></li>
            <li><a href="/secondData">Secondly data</a></li>
            <li><a href="/minuteData">Minutely data</a></li>
            <li><a href="/emojiData?q=10">Emoji data</a></li>
            <li><a href="/urlData?q=10">URL data</a></li>
            <li><a href="/hashtagData?q=10">Hashtag data</a></li>
            </ul>
            </body>
            </html>
          }
        }
      }
    }~
  path("totaltweets") {
    get {
      complete {
        s"${repository.getTweets.length} total tweets received."
      }
    }
  }~
  path("hourData") {
    get {
      complete {
        s"${getHourAvg} over ${getHours} hours"
      }
    }
  }~
  path("secondData") {
    get {
      complete {
        s"${getSecondAvg} over ${getSeconds} seconds"
      }
    }
  }~
  path("minuteData") {
    get {
      complete {
        s"${getMinuteAvg} over ${getMinutes} minutes"
      }
    }
  }~
  path("urlData") {
    get {
      parameters('q.as[Int]) { q =>
        onComplete(getTopUrl(q)) {
          case Success(urls) =>
            complete {
              s"${getUrlAvg}% contain urls\n${getPicAvg}% contains pictures\n\n" + urls.mkString("\n")
            }
          case Failure(ex) => complete("Error getting page")
        }
      }
    }
  }~
  path("hashtagData") {
    get {
      parameters('q.as[Int]) { q =>
        onComplete(getTopHashtags(q)) {
          case Success(hashtags) =>
            complete {
              s"${getHashAvg}% contains hashtags\n\n" + hashtags.mkString("\n")
            }
          case Failure(ex) => complete("Error getting page")
        }
      }
    }
  }~
  path("emojiData") {
    get {
      parameters('q.as[Int]) { q =>
        onComplete(getTopEmoji(q)) {
          case Success(emojis) =>
            complete(s"${getEmojiAvg}% contains emojis\n\n" + emojis.mkString("\n"))
          case Failure(ex) => complete("Error getting page")
        }
      }
    }
  }~
  path("timelyData") {
    get {
      complete {
        s"${getHourAvg} over ${getHours} hours\n${getMinuteAvg} over ${getMinutes} minutes\n${getSecondAvg} over ${getSeconds} seconds"
      }
    }
  }
}
