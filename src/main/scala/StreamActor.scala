package com.TwiTrav

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

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
    path("averages") {
      get {
        respondWithMediaType(`application/json`)
        complete {
          getAverages.toJson
        }
      }
    }~
    path("overtime") {
      get {
        respondWithMediaType(`application/json`)
        complete(getOvertime.toJson)
      }
    }~
    path("toplist") {
      get {
        parameters('q.as[Int]) { q =>
          respondWithMediaType(`application/json`)
          onComplete(getTopLists(q)) {
            case Success(topList) =>
              complete(topList.toJson)
            case Failure(ex) =>
              complete("""{"error":"could not complete"}""")
          }
        }
      }
    }
}
