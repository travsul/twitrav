import twitter4j._

trait TwitterConnection {
  def getConfig(consumerKey: String,
                consumerSecret: String,
                accessToken: String,
                accessTokenSecret: String): conf.Configuration = {
    new twitter4j.conf.ConfigurationBuilder()
                      .setOAuthConsumerKey(consumerKey)
                      .setOAuthConsumerSecret(consumerSecret)
                      .setOAuthAccessToken(accessToken)
                      .setOAuthAccessTokenSecret(accessTokenSecret)
                      .build()
  }

  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) {
      println(status.getText)
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) = ex.printStackTrace

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) {}
  }

  def getStream(config: conf.Configuration, searchQuery: Array[String]): TwitterStream = {
    val twitterStream = new TwitterStreamFactory(config).getInstance
    twitterStream.addListener(simpleStatusListener)
    twitterStream.filter(new FilterQuery().track(searchQuery))
    twitterStream
  }

  def closeStream(stream: TwitterStream) = {
    stream.cleanUp
    stream.shutdown
  }
}
object StatusStreamer extends TwitterConnection {
  val config = getConfig(consumerKey,consumerSecret,accessToken,accessTokenSecret)

  def main(args: Array[String]) {
    val stream = getStream(config,Array("melanie"))
    Thread.sleep(10000)
    closeStream(stream)
  }
}