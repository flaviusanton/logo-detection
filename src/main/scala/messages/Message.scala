package messages

import scala.util.Try
import org.apache.sling.commons.json.JSONException
import twitter4j.JSONObject

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
      throw new JSONException("Message type non existent: {msgType}.")

    new DownloadMessage(json.get("imageLink").toString())
  })
}

case class StoreMessage(imageLink: String="", binImage: Array[Byte]=null) extends Message {
  def getLink() = imageLink

  def getImage() = binImage

  def toJSON() = {
    new JSONObject()
  }

  def fromJSON(json: JSONObject) = Try({
    new StoreMessage("bla", null)
  })
}