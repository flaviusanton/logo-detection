package imgretrieve

import messages.{ DownloadMessage, KafkaClient, TestMessage }
import messages.serializers.MessageSerializers
import core.Downloader

object RunRetriever {

  val INTOPIC = "toDownload"
  val OUTTOPIC = "TODO"

  def main(args: Array[String]) {
    import MessageSerializers._

    val keeper = new DiskImageKeeper
    val downloader = Downloader
    val kafkaConsumer = new KafkaClient[DownloadMessage](INTOPIC)
    val kafkaProducer = new KafkaClient[TestMessage](OUTTOPIC)

    kafkaConsumer.consumerStart()

    while (true) {
      val msg = kafkaConsumer.receive()
      println(msg.imageLink)
      val bytes = downloader.downloadImage(msg.imageLink)

      if (bytes.isSuccess)
        keeper.putImage(msg.imageLink, bytes.get)
      else
        System.err.println(s"Failed to download image at: ${msg.imageLink}")
    }
  }

}