package com.TwiTrav

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

case class Secrets(consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String) {
  def toJson = {
    ("consumerKey" -> consumerKey)~
    ("consumerSecret" -> consumerSecret)~
    ("accessToken" -> accessToken)~
    ("accessTokenSecret" -> accessTokenSecret)
  }
}

case class StartStream(secrets: Secrets)
case object EmojiStream
case object SecondStream
case object MinuteStream
case object HourStream
case object UrlStream
