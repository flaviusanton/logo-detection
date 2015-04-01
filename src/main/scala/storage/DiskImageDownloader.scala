package storage
//As I mentioned in ImageDownloader comments this should just an implementation of the
// Storing/retrieving service
// No need for case class here
class DiskImageDownloader(path: String = "downloadedImages") extends ImageDownloader {
  //the code to actually download Images should be in the parent class
  def downloadImage(link: String): Unit = {
    //TODO
    println(s"Downloading ${link} to ${path}...")
  }
}