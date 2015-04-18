package core

import scala.util.Try
import java.net.{ HttpURLConnection, URL }

object Downloader {

  def downloadImage(link: String): Try[Array[Byte]] = Try({
    val url = new URL(link)
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")

    val in = conn.getInputStream
    val byteArray = Stream.continually(in.read()).takeWhile(-1 !=).map(_.toByte).toArray
    conn.disconnect()
    byteArray
  })

}