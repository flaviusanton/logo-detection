package storage

import messages.Message

case class DownloadMessage(imageLink: String) extends Message {
  def getLink() = imageLink
}