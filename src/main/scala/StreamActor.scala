package com.TwiTrav

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import spray.routing._
import spray.http._
import MediaTypes._
import StreamRepository._

class StreamActor extends Actor {
  def receive = {
    case AddTweet(tweet) => {
      addTweet(tweet)
      //println(getTweets)
    }
  }
}

class RouteActor extends Actor with StreamService {
  def actorRefFactory = context

  def receive = runRoute(streamRoute)
}

trait StreamService extends HttpService {
  val streamRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
            <body>
            <ul>
            <li><a href="/timelyData">Time data</a></li>
            <li><a href="/hourData">Hourly data</a></li>
            <li><a href="/secondData">Secondly data</a></li>
            <li><a href="/minuteData">Minutely data</a></li>
            <li><a href="/emojiData">Emoji data</a></li>
            <li><a href="/urlData">URL data</a></li>
            <li><a href="/hashtagData">Hashtag data</a></li>
            </ul>
            </body>
            </html>
          }
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
        s"${getSecondAvg} over ${getSeconds}"
      }
    }
  }~
  path("minuteData") {
    get {
      complete {
        s"${getMinuteAvg} over ${getMinutes}"
      }
    }
  }~
  path("urlData") {
    get {
      complete {
        s"${getUrlAvg}% contain urls\n${getPicAvg}% contains pictures\n\n" + getTopTenUrl.mkString("\n")
      }
    }
  }~
  path("hashtagData") {
    get {
      complete {
        s"${getHashAvg}% contains hashtags\n\n" + getTopTenHashtags.mkString("\n")
      }
    }
  }~
  path("timelyData") {
    get {
      complete {
        s"${getHourAvg} over ${getHours} hours\n${getMinuteAvg} over ${getMinutes}\n${getSecondAvg} over ${getSeconds}"
      }
    }
  }
}
