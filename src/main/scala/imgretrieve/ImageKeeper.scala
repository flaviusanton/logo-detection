package imgretrieve

import scala.util.Try
import java.io.File

trait ImageKeeper {

  def getImage(link: String): Try[File]

  def putImage(link: String, bytes: Array[Byte]): Try[File]
}