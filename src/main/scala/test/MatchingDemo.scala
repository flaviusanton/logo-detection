package test

import org.opencv.features2d.DescriptorExtractor
import org.opencv.features2d.Features2d
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Mat
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d.DescriptorMatcher
import org.opencv.core.MatOfDMatch
import org.opencv.highgui.Highgui
import reflect._

object MatchingDemo {

  val IMG_1 = "img1"
  val IMG_2 = "img2"


  def run() {
    println(s"\nRunning ${classTag[this.type].toString.replace("$", "")}")

    def detectAndExtract(mat: Mat) = {
      val keyPoints = new MatOfKeyPoint
      val detector = FeatureDetector.create(FeatureDetector.SURF)
      detector.detect(mat, keyPoints)

      println(s"There were ${keyPoints.toArray.size} KeyPoints detected")

      // Let's just use the best KeyPoints.
      val sorted = keyPoints.toArray.sortBy(_.response).reverse.take(50)
      // There isn't a constructor that takes Array[KeyPoint], so we unpack
      // the array and use the constructor that can take any number of
      // arguments.
      val bestKeyPoints: MatOfKeyPoint = new MatOfKeyPoint(sorted: _*)

      val extractor = DescriptorExtractor.create(DescriptorExtractor.SURF)
      val descriptors = new Mat
      extractor.compute(mat, bestKeyPoints, descriptors)

      println(s"${descriptors.rows} descriptors were extracted, each with dimension ${descriptors.cols}")

      (bestKeyPoints, descriptors)
    }

    val leftImage = Highgui.imread(IMG_1)
    val rightImage = Highgui.imread(IMG_2)

    // Detect KeyPoints and extract descriptors.
    val (leftKeyPoints, leftDescriptors) = detectAndExtract(leftImage)
    val (rightKeyPoints, rightDescriptors) = detectAndExtract(rightImage)

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE)
    val dmatches = new MatOfDMatch
    matcher.`match`(leftDescriptors, rightDescriptors, dmatches)

    val correspondenceImage = new Mat
    Features2d.drawMatches(leftImage, leftKeyPoints, rightImage, rightKeyPoints, dmatches, correspondenceImage)
    val filename = "matches.png"
    assert(Highgui.imwrite(filename, correspondenceImage))
  }
}
