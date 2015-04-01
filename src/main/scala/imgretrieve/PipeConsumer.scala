package imgretrieve

import scala.io.Source
import messages.Consumer
import messages.StoreMessage
import messages.DownloadMessage
import twitter4j.JSONObject
import messages.DownloadMessage

class PipeConsumer(fifoPath: String,
                   downloader: ImageDownloader,
                   producer: PipeProducer) extends Consumer {

  val reader = Source.fromFile(fifoPath).bufferedReader()

  def run() = {
    while (true) {
      val message = reader.readLine()

      if (message != null) {
        val json = new JSONObject(message)
        val downloadMessage = new DownloadMessage().fromJSON(json)
        
        if (downloadMessage.isSuccess) {
          val link = downloadMessage.get.asInstanceOf[DownloadMessage].getLink()
          val result = downloader.downloadImage(link)
        
          if (result.isSuccess) {
            val binaryImage = result.get
            val message = new StoreMessage(link, binaryImage)
            producer.send(message)
          }
        }
        
      }
    }

  }
}