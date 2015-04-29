package messages

import java.util.Properties
import kafka.javaapi.producer.Producer
import kafka.producer.{ KeyedMessage, ProducerConfig }
import messages.serializers.{ MessageJsonSerializer, MessageSerializers }
import kafka.consumer.ConsumerConfig
import kafka.consumer.Consumer
import java.util.HashMap
import messages.serializers.MessageJsonSerializer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kafka.consumer.KafkaStream
import java.util.concurrent.LinkedBlockingQueue
import kafka.producer.Partitioner

/**
 * This will be the Queue client for each component, having both producer and
 * consumer logic.
 */
class KafkaClient[M <: Message](topic: String)(implicit serializer: MessageJsonSerializer[M]) {

  val NUM_THREADS = 1 // shouldn't be more than 1 right now
  val KAFKA_POLL_INTERVAL = 500 // ms

  lazy val producer = new Producer[String, String](configProducer)
  lazy val consumer = Consumer.createJavaConsumerConnector(configConsumer)
  lazy val stream = consumerStart().get(0) // only one stream, cause we have one thread

  def configProducer(): ProducerConfig = {
    val props = new Properties

    props.put("metadata.broker.list", "localhost:9092")
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("request.required.acks", "1")
    props.put("partitioner.class", "messages.KafkaPartitioner")

    new ProducerConfig(props)
  }

  def configConsumer(): ConsumerConfig = {
    val props = new Properties

    props.put("group.id", "1")
    props.put("zookeeper.connect", "localhost:2181")
    props.put("zookeeper.session.timeout.ms", "400")
    props.put("zookeeper.sync.time.ms", "200")
    props.put("auto.commit.interval.ms", "1000")

    new ConsumerConfig(props)
  }

  def send(message: M) = {
    import MessageSerializers._

    val data = new KeyedMessage[String, String](topic, "0", serializer.serialize(message))
    producer.send(data)
  }

  def send(messages: Array[M]): Unit = {
    messages.foreach(send(_))
  }

  def consumerStart() = {
    val map = new HashMap[String, Integer]
    map.put(topic, NUM_THREADS)
    val consumerMap = consumer.createMessageStreams(map)
    consumerMap.get(topic)
  }

  def receive(): M = {
    val it = stream.iterator()

    while (!it.hasNext()) {
      Thread.sleep(KAFKA_POLL_INTERVAL)
    }

    val rawMsg = it.next().message()
    serializer.deserialize(new String(rawMsg.map(_.toChar)))
  }

}
