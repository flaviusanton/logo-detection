package imgretrieve

import messages.Consumer
import scala.io.Source


class DummyConsumer(fifoPath: String) extends Consumer {
  
  val reader = Source.fromFile(fifoPath).bufferedReader()

  def run() = {
    while (true) {
      val line = reader.readLine()
      println(line)
    }
  }

}