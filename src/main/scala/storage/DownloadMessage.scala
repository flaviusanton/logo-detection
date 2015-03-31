package storage

import messages.Message
//I would move this in the same file as Message and make Message sealed trait
//Add any further messages types there also
case class DownloadMessage(imageLink: String) extends Message {
  def getLink() = imageLink
}