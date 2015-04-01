package imgretrieve

import messages.Consumer
import scala.io.Source


class MemoryImageDownloader(fifoPath: String) extends ImageDownloader with Consumer {
  
  val reader = Source.fromFile(fifoPath).bufferedReader()

  def downloadImage(link: String): Unit = {
    println(s"Retrieving ${link}")
  }

  def run() = {
    while (true) {
      val link = reader.readLine()
      
      if (link != null)
        downloadImage(link)
    }
  }
}