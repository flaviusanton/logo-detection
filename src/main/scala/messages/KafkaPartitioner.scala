package messages

import kafka.producer.Partitioner
import kafka.utils.VerifiableProperties

class KafkaPartitioner(props: VerifiableProperties) extends Partitioner {
  var roundRobinCounter = 0

  def partition(key: Any, numPartitions: Int): Int = {
    roundRobinCounter = (roundRobinCounter + 1) % numPartitions
    roundRobinCounter
  }
}

