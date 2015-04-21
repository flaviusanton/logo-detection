package messages

import scala.util.Try
import twitter4j.JSONObject
import twitter4j.JSONException
import org.apache.commons.codec.binary.Base64

sealed trait Message

case class DownloadMessage(imageLink: String = "") extends Message

case class DetectMessage(imageLink: String = "") extends Message

case class StoreMessage(imageLink: String, binImage: Array[Byte]) extends Message

case class TestMessage(message: String) extends Message