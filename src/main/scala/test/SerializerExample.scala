package test

import messages.{TestMessage, DownloadMessage, Message}
import messages.serializers.{MessageSerializers, MessageJsonSerializer}

//the Queue client should look something like this
class SerializerClient[T <: Message](implicit serializer: MessageJsonSerializer[T]) {
  var x: String = ""
  def write(message: T) = {
    x = serializer.serialize(message)
    println(x)
  }
  def read(): T = {
    serializer.deserialize(x)
  }
}

object SerializerExample {

  def main(args: Array[String]) {
    //this import is needed to have the json serializers in scope when creating SerializerClient
    import MessageSerializers._

    //the json serializer for DownloadMessage is passed as implicit by the compiler
    val client1 = new SerializerClient[DownloadMessage]()
    client1.write(DownloadMessage("link_test"))
    println(client1.read().imageLink)

    //the json serializer for TestMessage is passed as implicit by the compiler
    val client2 = new SerializerClient[TestMessage]()
    client2.write(TestMessage("test"))
    println(client2.read().message)
  }
}
