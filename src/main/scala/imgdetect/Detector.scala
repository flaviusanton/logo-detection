package imgdetect

trait Detector {

  def detect(imageId: String): Array[String]

}