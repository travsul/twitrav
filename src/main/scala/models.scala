package com.TwiTrav

import org.json4s._
import org.json4s.jackson.JsonMethods._
import JsonDSL._
import JsonAST.JObject

case class Secrets(
  consumerKey: String,
  consumerSecret: String,
  accessToken: String,
  accessTokenSecret: String
) {
  def toJson: JObject = {
    ("consumerKey" -> consumerKey)~
    ("consumerSecret" -> consumerSecret)~
    ("accessToken" -> accessToken)~
    ("accessTokenSecret" -> accessTokenSecret)
  }
}

case class AddTweet(tweet: Tweet)
case class DeleteTweet(id: Long)

case class Emoji(
  name: String,
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

case class Tweet(
  id: Long,
  text: String,
  hasUrl: Boolean,
  hasEmoji: Boolean,
  hasHashtag: Boolean,
  urls: List[String],
  emojis: List[String],
  hashtags: List[String]
)

object Tweet {
  def empty: Tweet = {
    Tweet(0,"",false,false,false,Nil,Nil,Nil)
  }
}

case class Averages(
  url: Int,
  hashtag: Int,
  picture: Int,
  emoji: Int
) {
  def toJson: JObject = {
    ("url" -> url)~
    ("hashtag" -> hashtag)~
    ("picture" -> picture)~
    ("emoji" -> emoji)
  }
}

case class TopList(
  url: List[Occurrence],
  hashtag: List[Occurrence],
  emoji: List[Occurrence]
) {
  def toJson: JObject = {
    val urlJson = s"""[${url.map(s=>compact(render(s.toJson))).mkString(",")}]"""
    val hashtagJson = s"""[${hashtag.map(s=>compact(render(s.toJson))).mkString(",")}]"""
    val emojiJson = s"""[${emoji.map(s=>compact(render(s.toJson))).mkString(",")}]"""

    ("url" -> urlJson)~
    ("hashtag" -> hashtagJson)~
    ("emoji" -> emojiJson)
  }
}

case class Occurrence(
  item: String,
  uses: Int
) {
  def toJson: JObject = {
    ("item" -> item)~
    ("uses" -> uses)
  }
}

case class Overtime(
  seconds: Int,
  minutes: Int,
  hours: Int
) {
  def toJson: JObject = {
    ("seconds" -> seconds)~
    ("minutes" -> minutes)~
    ("hours" -> hours)
  }
}
