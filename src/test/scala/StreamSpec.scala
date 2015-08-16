package com.TwiTrav

import org.specs2.mutable.Specification
import org.specs2.specification.{Scope, After, BeforeAfterEach}
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

object StreamSpec extends Specification with Specs2RouteTest with StreamService {
  def actorRefFactory = system

  "Spray routes" should {
    "The front page contains links" in {
      Get() ~> streamRoute ~> check {
        responseAs[String] must contain("Time data")
        responseAs[String] must contain("Hourly data")
        responseAs[String] must contain("Minutely data")
        responseAs[String] must contain("Emoji data")
        responseAs[String] must contain("Secondly data")
        responseAs[String] must contain("URL data")
        responseAs[String] must contain("Hashtag data")
      }
    }
    "Hourly data contains correct layout" in {
      Get("/hourData") ~> streamRoute ~> check {
        responseAs[String] must contain("over")
        responseAs[String] must contain("hours")
      }
    }
    "Second data contains correct layout" in {
      Get("/secondData") ~> streamRoute ~> check {
        responseAs[String] must contain("over")
        responseAs[String] must contain("seconds")
      }
    }
    "Minute data contains correct layout" in {
      Get("/minuteData") ~> streamRoute ~> check {
        responseAs[String] must contain("over")
        responseAs[String] must contain("minutes")
      }
    }
    "Hashtage data contains correct layout" in {
      Get("/hashtagData") ~> streamRoute ~> check {
        responseAs[String] must contain("contains hashtags")
      }
    }
    "Url data contains correct layout" in {
      Get("/urlData") ~> streamRoute ~> check {
        responseAs[String] must contain("contain urls")
        responseAs[String] must contain("contains pictures")
      }
    }
    "Emoji data contains correct layout" in {
      Get("/emojiData") ~> streamRoute ~> check {
        responseAs[String] must contain("contains emojis")
      }
    }
  }
}
