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

sealed trait GetStream
case class EmojiStream(secrets: Secrets) extends GetStream
case class NumberStream(secrets: Secrets) extends GetStream
case class UrlStream(secrets: Secrets) extends GetStream
