package messages

trait Producer {
  def send(message: Message): Unit
  def send(messages: Array[Message]): Unit
}