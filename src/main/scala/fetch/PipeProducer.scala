package fetch

import messages.Producer
import messages.DownloadMessage
import java.io.PrintWriter
import java.io.File

class DummyProducer(fifoPath: String) extends Producer[DownloadMessage] {

  val pw = new PrintWriter(new File(fifoPath))

  def send(message: DownloadMessage): Unit = {
    message match {
      case msg: DownloadMessage => write(msg)
      case _ => throw new ClassCastException
    }
  }

  def write(message: DownloadMessage) = {
    pw.write(message.getLink() + "\n")
    pw.flush()
  }
  
}