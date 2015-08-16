package com.TwiTrav

import org.specs2._
import scala.concurrent._

object RepositorySpec extends mutable.Specification with TweetFunctions {
  val repository = RepositoryConnection.repository
  sequential

  lazy val newTweet = Tweet(1234,"",true,true,true,List("www.fun.com"),List(":)"),List("#fun"))
  lazy val adding   = repository.addTweet(newTweet)
  lazy val deleting = repository.deleteTweet(1234)

  "Tweet repository adding" should {
    "Have an initialized repository" in {
      repository.getTweets must_== Nil
    }
    "Add a correct tweet" in {
      repository.addTweet(newTweet)
      repository.getTweets must_== List(newTweet)
    }
  }

  "Getting all emojis" should {
    "Get correct emojis" in {
      repository.getEmojis must be_==(List(":)")).await
    }
  }

  "Getting all domains" should {
    "Get correct domains" in {
      repository.getUrls must be_==(List("www.fun.com")).await
    }
  }

  "Getting all hashtags" should {
    "Get correct hashtags" in {
      repository.getHashtags must be_==(List("#fun")).await
    }
  }

  "Tweet repository deleting" should {
    "Return the deleted tweet" in {
      repository.deleteTweet(1234) must be_==(Some(newTweet)).await
    }
    "Delete the tweet correctly" in {
      repository.getTweets must_== Nil
    }
  }
}
