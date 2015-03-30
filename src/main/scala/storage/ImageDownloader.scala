package storage

trait ImageDownloader {
  def downloadImage(link: String): Unit;
}