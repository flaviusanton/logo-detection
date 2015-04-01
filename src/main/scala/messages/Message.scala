package messages

import scala.util.Try
import twitter4j.JSONObject
import twitter4j.JSONException
import org.apache.commons.codec.binary.Base64

sealed trait Message {
  def toJSON(): JSONObject

  def fromJSON(json: JSONObject): Try[Message]

  override def toString(): String = {
    this.toJSON.toString
  }
}

case class DownloadMessage(imageLink: String="") extends Message {
  def getLink() = imageLink

  def toJSON() = {
    val json = new JSONObject
    json.put("type", this.getClass.getName)
    json.put("imageLink", imageLink)
  }

  def fromJSON(json: JSONObject): Try[Message] = Try({
    val msgType = json.get("type")

    if (msgType != this.getClass.getName)
      throw new JSONException("Message type non existent: ${msgType}.")

    new DownloadMessage(json.get("imageLink").toString())
  })
}

case class StoreMessage(imageLink: String="", binImage: Array[Byte]=null) extends Message {
  def getLink() = imageLink

  def getImage() = binImage

  def toJSON() = {
    val json = new JSONObject()
    json.put("type", this.getClass.getName)
    json.put("imageLink", imageLink)

    val base64 = Base64.encodeBase64String(binImage)
    json.put("binImage", base64)
  }

  def fromJSON(json: JSONObject) = Try({
    val msgType = json.get("type")

    if (msgType != this.getClass.getName)
      throw new JSONException("Message type non existent: ${msgType}.")

    val imageLink = json.get("imageLink").toString()
    val base64Image = json.get("binImage").toString()
    val binImage = Base64.decodeBase64(base64Image)

    new StoreMessage(imageLink, binImage)
  })
}