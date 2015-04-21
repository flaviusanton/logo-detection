package imgretrieve

import scala.util.Try
import java.net.{ HttpURLConnection, URL }
import java.io.File

trait ImageKeeper {

  protected def downloadImage(link: String): Try[Array[Byte]] = Try({
    val url = new URL(link)
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")

    val in = conn.getInputStream
    val byteArray = Stream.continually(in.read()).takeWhile(-1 !=).map(_.toByte).toArray
    conn.disconnect()
    byteArray
  })

  def getImage(link: String): Try[File]

  def putImage(link: String)
}