package imgretrieve

import messages.Producer
import messages.StoreMessage
import java.io.File
import java.io.DataOutputStream
import messages.StoreMessage
import java.io.FileOutputStream

class PipeProducer(fifoPath: String) extends Producer[StoreMessage] {
  val writer = new DataOutputStream(new FileOutputStream(new File(fifoPath)))

  def send(message: StoreMessage): Unit = {
    message match {
      case msg: StoreMessage => write(msg)
      case _ => throw new ClassCastException
    }
  }

  def write(msg: StoreMessage) = {
    println(s"Sending to storage: {msg.getLink}")
  }
  
}