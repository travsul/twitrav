package com.TwiTrav

import org.json4s.JsonDSL._

case class Secrets(
  consumerKey: String,
  consumerSecret: String,
  accessToken: String,
  accessTokenSecret: String
) {
  def toJson = {
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

case class Averages(
  url: Int,
  hashtag: Int,
  picture: Int,
  emoji: Int
) {
  def toJson: String = {
    s"""{"url":$url,"hashtag":$hashtag,"picture":$picture,"emoji":$emoji}"""
  }
}

case class TopList(
  url: List[Occurrence],
  hashtag: List[Occurrence],
  emoji: List[Occurrence]
) {
  def toJson: String = {
    val urlJson = s"""[${url.map(_.toJson).mkString(",")}]"""
    val hashtagJson = s"""[${hashtag.map(_.toJson).mkString(",")}]"""
    val emojiJson = s"""[${emoji.map(_.toJson).mkString(",")}]"""
    s"""{"url":$urlJson,"hashtag":$hashtagJson,"emoji":$emojiJson}"""
  }
}

case class Occurrence(
  item: String,
  uses: Int
) {
  def toJson: String = {
    s"""{"item":$item,"uses":$uses}"""
  }
}

case class Overtime(
  seconds: Int,
  minutes: Int,
  hours: Int
) {
  def toJson: String = {
    s"""{"seconds":$seconds,"minutes":$minutes,"hours":$hours}"""
  }
}
