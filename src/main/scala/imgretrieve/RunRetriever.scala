package imgretrieve

import messages.{ DownloadMessage, KafkaClient, TestMessage }
import messages.serializers.MessageSerializers

object RunRetriever {

  val INTOPIC = "toDownload"
  val OUTTOPIC = "TODO"

  def main(args: Array[String]) {
    import MessageSerializers._

    val keeper = new DiskImageKeeper
    val kafkaConsumer = new KafkaClient[DownloadMessage](INTOPIC)
    val kafkaProducer = new KafkaClient[TestMessage](OUTTOPIC)

    kafkaConsumer.consumerStart()

    while (true) {
      val msg = kafkaConsumer.receive()
      println(msg.imageLink)
      keeper.putImage(msg.imageLink)
    }
  }

}