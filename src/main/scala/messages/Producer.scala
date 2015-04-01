package messages

trait Producer[T <: Message] {
  def send(message: T): Unit

  def send(messages: Array[T]): Unit = {
    messages.foreach(send(_))
  }
}