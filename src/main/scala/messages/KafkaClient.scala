package messages

import java.util.Properties

import kafka.javaapi.producer.Producer
import kafka.producer.{ KeyedMessage, ProducerConfig }
import messages.serializers.{ MessageJsonSerializer, MessageSerializers }

/**
 * This will be the Queue client for each component, having both producer and
 * consumer logic.
 */
class KafkaClient[T <: Message](topic: String)(implicit serializer: MessageJsonSerializer[T]) {

  val producerConfig = new ProducerConfig(configProducer)
  lazy val producer = new Producer[String, String](producerConfig)

  def configProducer(): Properties = {
    val props = new Properties

    props.put("metadata.broker.list", "localhost:9092")
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("request.required.acks", "1")

    props
  }

  def send(message: T) = {
    import MessageSerializers._

    val data = new KeyedMessage[String, String](topic, serializer.serialize(message))
    producer.send(data)
  }

  def send(messages: Array[T]): Unit = {
    messages.foreach(send(_))
  }

  def receive(): T = {
    ???
  }

}