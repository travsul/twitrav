package com.TwiTrav

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import twitter4j._

case class Secrets(consumerKey: String, consumerSecret: String, accessToken: String, accessTokenSecret: String) {
  def toJson = {
    ("consumerKey" -> consumerKey)~
    ("consumerSecret" -> consumerSecret)~
    ("accessToken" -> accessToken)~
    ("accessTokenSecret" -> accessTokenSecret)
  }
}

case class AddTweet(tweet: Status)

case class Emoji(name: String,
  unified: String,
  variations: List[String],
  docomo: String,
  au: String,
  softbank: String,
  google: String,
  image: String,
  sheet_x: String,
  sheet_y: String,
  short_name: String,
  short_names: List[String],
  text: String,
  texts: List[String],
  category: String,
  sort_order: String,
  has_img_apple: Boolean,
  has_img_google: Boolean,
  has_img_twitter: Boolean,
  has_img_emojione: Boolean
)
