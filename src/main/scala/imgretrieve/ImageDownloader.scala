package imgretrieve

// let's decouple the downloading of images from storing them
// you can create a trait that will handle storing and loading images
// and make ImageDownloader depend on the new trait
// this can be for now local disk but in the future it could be S3
trait ImageDownloader {
  def downloadImage(link: String): Unit;
}