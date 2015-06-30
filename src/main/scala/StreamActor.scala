package com.TwiTrav

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class StreamActor extends Actor with TwitterConnection {
  def receive = {
    case EmojiStream(secrets) => getStream(secrets)
    case NumberStream(secrets) => getStream(secrets)
    case UrlStream(secrets) => getStream(secrets)
  }
}
