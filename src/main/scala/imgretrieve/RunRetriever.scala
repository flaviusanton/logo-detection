package imgretrieve

import messages.{ DownloadMessage, DetectMessage, KafkaClient }
import core.Downloader
import messages.DetectMessage
import messages.serializers.MessageSerializers

object RunRetriever {

  val INTOPIC = "toDownload"
  val OUTTOPIC = "toDetect"

  def main(args: Array[String]) {
    import MessageSerializers._

    val keeper = new DiskImageKeeper
    val downloader = Downloader
    val kafkaConsumer = new KafkaClient[DownloadMessage](INTOPIC)
    val kafkaProducer = new KafkaClient[DetectMessage](OUTTOPIC)

    //kafkaConsumer.consumerStart()

    while (true) {
      val msg = kafkaConsumer.receive()
      println(msg.imageLink)
      val bytes = downloader.downloadImage(msg.imageLink)

      if (bytes.isSuccess) {
        keeper.putImage(msg.imageLink, bytes.get)
        val detectMsg = new DetectMessage(msg.imageLink)
        kafkaProducer.send(detectMsg)
      } else {
        System.err.println(s"Failed to download image at: ${msg.imageLink}")
      }
    }
  }

}