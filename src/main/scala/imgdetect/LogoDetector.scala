package imgdetect

import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d.DescriptorExtractor
import org.opencv.highgui.Highgui
import org.opencv.features2d.DescriptorMatcher
import org.opencv.core.MatOfDMatch
import org.opencv.features2d.Features2d
import scala.collection.JavaConverters._
import imgretrieve.DiskImageKeeper
import java.io.File
import scala.collection.mutable.HashMap
import scala.util.Try

class LogoDetector(trainLogos: Array[String]) extends Detector {

  val LOGODIR = "/train-logos/"
  private val keeper = new DiskImageKeeper
  private val trainImgMap = new HashMap[String, Array[String]]

  trainLogos.foreach { logo =>
    val logoDir = new File(getClass.getResource(LOGODIR + logo).getPath)
    val logoImgs = logoDir.listFiles.map(_.getAbsolutePath)
    trainImgMap.put(logo, logoImgs)
  }

  def detect(imageLink: String): Array[String] = {
    val imFile = keeper.getImage(imageLink)
    if (imFile.isSuccess) {
      trainLogos.foreach { logo =>
        val trainImgs = trainImgMap.get(logo)

        trainImgs match {
          case None => System.err.println(s"Cannot find logo in Logo Map.")
          case Some(x) => {
            val score = detectScore(imFile.get.getAbsolutePath, x)
            println(s"Score with ${logo} for ${imageLink}: ${score}")
          }
        }

      }
    } else {
      System.err.println(s"Failed to load image ${imageLink}")
    }

    return Array("string")
  }

  def detectScore(imgQuery: String, trainImgs: Array[String]): Double = {

    val queryImage = Highgui.imread(imgQuery)
    val (leftKeyPoints, leftDescriptors) = detectAndExtract(queryImage)

    val trainList: java.util.List[Mat] = new java.util.ArrayList()

    trainImgs.foreach { img =>
      val imgMat = Highgui.imread(img)
      val (imgKeyPoints, imgDescriptors) = detectAndExtract(imgMat)
      trainList.add(imgDescriptors)
    }

    val matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE)
    val dmatches = new MatOfDMatch
    matcher.add(trainList)
    matcher.train()

    Try(matcher.`match`(leftDescriptors, dmatches)).getOrElse {
      System.err.println("Error matching images (Size stuff)");
      return -1
    }

    //dmatches.toList().asScala.sortWith((e1, e2) => e1.distance < e2.distance).take(200).foreach(println)

    val distances = dmatches.toArray().map(x => x.distance)

    val count = distances.length
    val mean = distances.sum / count

    mean
  }

  def detectAndExtract(mat: Mat) = {
    val keyPoints = new MatOfKeyPoint
    val detector = FeatureDetector.create(FeatureDetector.SURF)
    detector.detect(mat, keyPoints)
    val sorted = keyPoints.toArray.sortBy(_.response).reverse.take(50)
    val bestKeyPoints: MatOfKeyPoint = new MatOfKeyPoint(sorted: _*)
    val extractor = DescriptorExtractor.create(DescriptorExtractor.SURF)
    val descriptors = new Mat
    extractor.compute(mat, bestKeyPoints, descriptors)
    //println(s"${descriptors.rows} descriptors were extracted, each with dimension ${descriptors.cols}")
    (bestKeyPoints, descriptors)
  }
}