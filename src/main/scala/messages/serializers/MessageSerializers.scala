package messages.serializers

import messages._
import spray.json.{ DefaultJsonProtocol, RootJsonFormat, _ }

object MessageJsonProtocol extends DefaultJsonProtocol {
  implicit val downloadMessageFormat = jsonFormat1(DownloadMessage)
  implicit val detectMessageFormat = jsonFormat1(DetectMessage)

  implicit val testMessageFormat = jsonFormat1(TestMessage)
}

class MessageJsonSerializer[T <: Message](implicit format: RootJsonFormat[T]) {
  def serialize(message: T): String = {
    message.toJson.compactPrint
  }

  def deserialize(json: String): T = {
    json.parseJson.convertTo[T]
  }
}

object MessageSerializers {
  import MessageJsonProtocol._
  implicit lazy val downloadMessageSerializer = new MessageJsonSerializer[DownloadMessage]()
  implicit lazy val detectMessageSerializer = new MessageJsonSerializer[DetectMessage]()

  implicit lazy val testMessageSerializer = new MessageJsonSerializer[TestMessage]()
}

