package imgretrieve

import java.net.URL
import java.net.HttpURLConnection
import java.io.IOException
import java.io.FileNotFoundException
import scala.util.Try

trait ImageDownloader {

  def downloadImage(link: String): Try[Array[Byte]] = Try({
    val url = new URL(link)
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")

    val in = conn.getInputStream
    val byteArray = Stream.continually(in.read()).takeWhile(-1 != ).map(_.toByte).toArray
    conn.disconnect()
    byteArray
  })

}