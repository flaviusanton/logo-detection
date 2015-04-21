package fetch

import services.TwitterService
import twitter4j.Status
import messages.DownloadMessage
import messages.serializers.{ MessageSerializers, MessageJsonSerializer }
import messages.KafkaClient
import messages.DownloadMessage

object Main {

  val TOPIC = "toDownload"

  def main(args: Array[String]) {
    import MessageSerializers._

    val fetcher = new TwitterFetcher().connect(TwitterService).startAsyncFetch()
    val kafka = new KafkaClient[DownloadMessage, DownloadMessage](TOPIC, null)

    while (true) {
      val urls = fetcher.consume(fetcher.CHUNK_SIZE).flatMap { x =>
        x match {
          case status: Status => status.getMediaEntities.map { x => x.getMediaURL }
          case _              => throw new ClassCastException
        }
      }

      val messages = urls.map(new DownloadMessage(_))
      kafka.send(messages)
    }
  }
}