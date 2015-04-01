package messages

sealed trait Message

case class DownloadMessage(imageLink: String) extends Message {
  def getLink() = imageLink
}