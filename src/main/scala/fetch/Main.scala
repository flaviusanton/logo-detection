package fetch

import services.TwitterService
import twitter4j.Status
import storage.DownloadMessage

object Main {
  
  def main(args: Array[String]) {
    val fetcher = new TwitterFetcher().connect(TwitterService).startAsyncFetch()
    val producer = new DummyProducer()
    
    while (true) {
      val urls = fetcher.consume(fetcher.CHUNK_SIZE).flatMap { x =>
        x match {
          case status: Status => status.getMediaEntities.map { x =>  x.getMediaURL}
          case _ => throw new ClassCastException
        }
      }
      
      val messages = urls.map(new DownloadMessage(_))
      messages.map(producer.send(_))
    }
  }
}