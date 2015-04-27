package imgdetect

trait Detector {

  def detect(imageLink: String): Array[String]

}