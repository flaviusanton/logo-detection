package imgretrieve


class DiskImageDownloader extends ImageDownloader {
  
  def downloadImage(link: String): Unit = {
    println(s"Retrieving ${link}")
  }
  
}