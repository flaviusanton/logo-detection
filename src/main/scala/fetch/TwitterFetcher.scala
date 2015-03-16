package fetch

import services.Service
import services.TwitterService
import twitter4j._
import twitter4j.conf.ConfigurationBuilder
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.BlockingQueue

class TwitterFetcher extends Fetcher {

  private val configFilename = "config/twitter.config"
  private var streamer: TwitterStream = _
  private var limit: Integer = 100
  private val queue: BlockingQueue[Status] = new LinkedBlockingQueue()

  def connect(service: Service): Fetcher = {
    val map = service.loadConfig(configFilename)

    streamer = service.buildConfig(map) match {
      case configBuilder: ConfigurationBuilder => new TwitterStreamFactory(configBuilder.build()).getInstance
      case _ => throw new ClassCastException
    }

    streamer.addListener(streamListener)
    this
  }

  def setLimit(limit: Integer): Fetcher = {
    this.limit = limit
    this
  }

  def startAsyncFetch(): Fetcher = {
    streamer.sample()
    this
  }

  def consume(n: Integer): Seq[Any] = {
    var arr: Array[Status] = Array()
    (1 to n) foreach (_ => arr :+= queue.take)
    arr
  }

  def streamListener = new StatusListener() {
    def onStatus(status: Status) {
      if (queue.size() < limit) {
        if (!status.getMediaEntities.isEmpty)
          queue.offer(status)
      }
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }
}
