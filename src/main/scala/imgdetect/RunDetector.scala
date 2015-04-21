package imgdetect

import imgretrieve.DiskImageKeeper
import messages.{ DetectMessage, KafkaClient, TestMessage }
import messages.serializers.MessageSerializers

object RunDetector {

  val INTOPIC = "toDetect"
  val OUTTOPIC = "TODO"
  val trainLogos = Array("google", "facebook", "hootsuite")

  def main(args: Array[String]) {
    import MessageSerializers._

    val keeper = new DiskImageKeeper
    val detector = new LogoDetector(trainLogos)
    val kafkaConsumer = new KafkaClient[DetectMessage](INTOPIC)
    val kafkaProducer = new KafkaClient[TestMessage](OUTTOPIC)

    kafkaConsumer.consumerStart()

    while (true) {
      val msg = kafkaConsumer.receive()
      detector.detect(msg.imageLink)
    }
  }
}