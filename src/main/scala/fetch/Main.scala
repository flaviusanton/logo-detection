package fetch

import services.TwitterService
import twitter4j.Status
import messages.DownloadMessage

object Main {
  
  val FIFO_PATH = "links.fifo"

  def main(args: Array[String]) {
    val fetcher = new TwitterFetcher().connect(TwitterService).startAsyncFetch()
    val producer = new DummyProducer(FIFO_PATH)
    
    while (true) {
      val urls = fetcher.consume(fetcher.CHUNK_SIZE).flatMap { x =>
        x match {
          case status: Status => status.getMediaEntities.map { x =>  x.getMediaURL}
          case _ => throw new ClassCastException
        }
      }
      
      val messages = urls.map(new DownloadMessage(_))
      producer.send(messages)
    }
  }
}