package fetch

import services.Service
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

class TwitterFetcher extends Fetcher {

  private val configFilename = "config/twitter.config"

  def connect(service: Service): Boolean = {
    val map = service.loadConfig(configFilename)
    val streamer = service.buildConfig(map) match {
      case configBuilder: ConfigurationBuilder => new TwitterStreamFactory(configBuilder.build()).getInstance
      case _ => throw new ClassCastException
    }
    
    streamer.addListener(simpleStatusListener)
    streamer.sample()
    
    Thread.sleep(100000)
    true
  }

  def setLimit(limit: Integer): Unit = {
    ???
  }

  def startAsyncFetch(): Unit = {
    ???
  }

  def take(n: Integer): Unit = {
    ???
  }
  
  def simpleStatusListener = new StatusListener() {
    def onStatus(status: Status) {
      if (!status.getMediaEntities.isEmpty) {
        status.getMediaEntities.map { x => println(x.getMediaURL) }
      }
      if (!status.getExtendedMediaEntities.isEmpty) {
        status.getExtendedMediaEntities.map { x => println(x.getMediaURL) }
      }
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }
}
