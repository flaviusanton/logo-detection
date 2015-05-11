package test

import services.TwitterService
import fetch.TwitterFetcher
import twitter4j.Status
import org.opencv.core.Core
import imgdetect.LogoDetector
import java.io.File

object Main {

  val MAX_ITER = 1000

  def main(args: Array[String]) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    //testCV
    testTemplateMatcher
  }

  def testCV = {

    val f = new File("src/main/resources/train-logos/honda")
    val detector = new TemplateMatcher(f.listFiles.map(_.getAbsolutePath))

    //detector.detect("query.jpg").map(println)

    val g = new File("twitter-images/100imgs")
    var count = 0

    for (file <- g.listFiles) {
      println(s"***** CHECKING ${file} *****")
      val result = detector.detect(file.getAbsolutePath)
      result.map(println(_))

      count += result.length
    }

    println(s"Identified: ${count}")
  }

  def testTemplateMatcher = {
    val logoDir = new File("src/main/resources/train-logos/honda")
    val detector = new TemplateDetector(Array("honda"))

    //detector.tryAllMatches("query/honda/honda-civic-new-car-smell-537x334.jpg", logoDir.listFiles.map(_.getAbsolutePath))
    val g = new File("/tmp/tw-images")
    var count = 0

    for (file <- g.listFiles) {
      println(s"***** CHECKING ${file} *****")
      val result = detector.tryAllMatches(file.getAbsolutePath, logoDir.listFiles.map(_.getAbsolutePath))

      if (result)
        count += 1
    }

    println(s"Identified: ${count}")
  }
}
