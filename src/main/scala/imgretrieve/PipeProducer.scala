package imgretrieve

import messages.Producer
import messages.StoreMessage
import java.io.File
import messages.StoreMessage
import java.io.PrintWriter

class PipeProducer(fifoPath: String) extends Producer[StoreMessage] {
  val pw = new PrintWriter(new File(fifoPath))

  def send(message: StoreMessage): Unit = {
    message match {
      case msg: StoreMessage => write(msg)
      case _ => throw new ClassCastException
    }
  }

  def write(msg: StoreMessage) = {
    pw.write(msg + "\n")
  }
  
}