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
import java.util.concurrent.ArrayBlockingQueue

/**
 * This will be the Queue client for each component, having both producer and
 * consumer logic.
 */
class KafkaClient[M <: Message](topic: String)(implicit serializer: MessageJsonSerializer[M]) {

  val NUM_THREADS = 4

  lazy val producer = new Producer[String, String](configProducer)
  lazy val consumer = Consumer.createJavaConsumerConnector(configConsumer)
  lazy val executor = Executors.newFixedThreadPool(NUM_THREADS)

  //TODO: find a better solution
  // limit queue capacity to 1 to avoid loss of messages
  lazy val consumerQ = new ArrayBlockingQueue[M](1)

  def configProducer(): ProducerConfig = {
    val props = new Properties

    props.put("metadata.broker.list", "localhost:9092")
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("request.required.acks", "1")

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

    val data = new KeyedMessage[String, String](topic, serializer.serialize(message))
    producer.send(data)
  }

  def send(messages: Array[M]): Unit = {
    messages.foreach(send(_))
  }

  def consumerStart(): Unit = {
    val map = new HashMap[String, Integer]
    map.put(topic, NUM_THREADS)
    val consumerMap = consumer.createMessageStreams(map)
    val streams = consumerMap.get(topic)

    import scala.collection.JavaConversions._
    streams.foreach(x => executor.submit(new Work(x)))
  }

  def receive(): M = consumerQ.take

  class Work(stream: KafkaStream[Array[Byte], Array[Byte]]) extends Runnable {

    def run() {
      val it = stream.iterator()

      while (it.hasNext()) {
        val rawMsg = it.next().message()
        val msg: M = serializer.deserialize(new String(rawMsg.map(_.toChar)))
        consumerQ.offer(msg)
      }
    }
  }
}