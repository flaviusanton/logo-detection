package storage

case class DiskImageDownloader(path: String = "downloadedImages") extends ImageDownloader {
  def downloadImage(link: String): Unit = {
    //TODO
    println(s"Downloading ${link} to ${path}...")
  }
}