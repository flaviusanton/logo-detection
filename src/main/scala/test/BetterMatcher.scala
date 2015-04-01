package test

import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Mat
import org.opencv.features2d.FeatureDetector
import org.opencv.features2d.DescriptorExtractor
import org.opencv.highgui.Highgui
import org.opencv.features2d.DescriptorMatcher
import org.opencv.core.MatOfDMatch
import scala.collection.JavaConverters._


object BetterMatcher {
  def run(trainImgs: Array[String], pathToImgQuery: String) = {

    def detectAndExtract(mat: Mat) = {
      val keyPoints = new MatOfKeyPoint
      val detector = FeatureDetector.create(FeatureDetector.ORB)
      detector.detect(mat, keyPoints)

      //println(s"There were ${keyPoints.toArray.size} KeyPoints detected")

      val bestKeyPoints: MatOfKeyPoint = new MatOfKeyPoint(keyPoints.toArray: _*)

      val extractor = DescriptorExtractor.create(DescriptorExtractor.ORB)
      val descriptors = new Mat
      extractor.compute(mat, bestKeyPoints, descriptors)

      //println(s"${descriptors.rows} descriptors were extracted, each with dimension ${descriptors.cols}")

      (bestKeyPoints, descriptors)
    }

    val queryImage = Highgui.imread(pathToImgQuery)
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
    matcher.`match`(leftDescriptors, dmatches)
    
    dmatches.toList().asScala.sortWith((e1, e2) => e1.distance < e2.distance).take(200).foreach(println)
    
    val distances = dmatches.toArray().map(x => x.distance)
    
    val count = distances.length
    val mean = distances.sum / count
    println(mean)
    
  }
}