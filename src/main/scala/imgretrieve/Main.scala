package imgretrieve

import messages.{ DownloadMessage, KafkaClient, TestMessage }
import messages.serializers.MessageSerializers

object Main {

  val INTOPIC = "toDownload"
  val OUTTOPIC = "TODO"

  def main(args: Array[String]) {
    import MessageSerializers._

    val keeper = new DiskImageKeeper
    val kafka = new KafkaClient[TestMessage, DownloadMessage](OUTTOPIC, INTOPIC)

    kafka.consumerStart()

    while (true) {
      val msg = kafka.receive
      println(msg.imageLink)
      keeper.putImage(msg.imageLink)
    }
  }

}