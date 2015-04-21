package imgretrieve

import scala.util.Try

abstract class MemoryImageDownloader extends ImageKeeper {

  override def downloadImage(link: String): Try[Array[Byte]] = {
    println(s"Retrieving ${link}")
    super.downloadImage(link)
  }

}