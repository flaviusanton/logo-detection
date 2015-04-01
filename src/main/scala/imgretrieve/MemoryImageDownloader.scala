package imgretrieve

import scala.util.Try

class MemoryImageDownloader extends ImageDownloader {
  
  override def downloadImage(link: String): Try[Array[Byte]] = {
    println(s"Retrieving ${link}")
    super.downloadImage(link)
  }

}