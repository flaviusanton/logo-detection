package storage

import messages.serializers.MessageSerializers
import messages.{ KafkaClient, StoreDetectedMessage }

object RunStorage {

  val TOPIC = "toStoreDetected"

  def main(args: Array[String]) {
    import MessageSerializers._

    val kafkaConsumer = new KafkaClient[StoreDetectedMessage](TOPIC)
    val db = MemDB

    kafkaConsumer.consumerStart()

    while (true) {
      val msg = kafkaConsumer.receive()
      db.store(msg.imageLink, msg.detectedLogos)
    }
  }
}