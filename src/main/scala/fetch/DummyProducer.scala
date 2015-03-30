package fetch

import messages.Producer
import messages.Message
import storage.DownloadMessage


class DummyProducer extends Producer {
  def send(messages: Array[Message]): Unit = {
    messages.map(_ match {
      case msg: DownloadMessage => println(msg.getLink())
      case _ => throw new ClassCastException
    }
  )}

  def send(message: Message): Unit = {
    message match {
      case msg: DownloadMessage => println(msg.getLink())
      case _ => throw new ClassCastException
    }
  }
}