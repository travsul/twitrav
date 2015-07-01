package com.TwiTrav

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.duration._
import scala.language.postfixOps

class StreamActor extends Actor with TwitterConnection with StreamRepository {
  import context._
  val system = StatusStreamer.system

  def receive = {
    case StartStream(secrets) => {
      getStream(secrets).sample
      system.scheduler.scheduleOnce(1 seconds,self,SecondStream)
      system.scheduler.scheduleOnce(1 minutes,self,MinuteStream)
      system.scheduler.scheduleOnce(1 hours,self,HourStream)
    }
    case SecondStream => {
      system.scheduler.scheduleOnce(1 seconds,self,SecondStream)
      println("Second average: " + getSecondAvg)
    }
    case MinuteStream => {
      system.scheduler.scheduleOnce(1 minutes,self,MinuteStream)
      println("Minute average: " + getMinuteAvg)
      println("Url average: " + getUrlAvg)
    }
    case HourStream => {
      system.scheduler.scheduleOnce(1 hours,self,HourStream)
      println("Hour average: " + getHourAvg)
    }
  }
}
