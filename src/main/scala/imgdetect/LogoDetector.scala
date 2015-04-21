package imgdetect

class LogoDetector(trainLogos: Array[String]) extends Detector {

  def detect(imageLink: String): Array[String] = {
    val result = new Array[String](0)

    println(s"Detecting ${imageLink}")
    return result

  }
}