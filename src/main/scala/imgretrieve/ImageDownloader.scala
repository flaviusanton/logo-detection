package imgretrieve

trait ImageDownloader {
  def downloadImage(link: String): Unit;
}