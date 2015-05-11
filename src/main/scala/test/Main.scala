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
    testCV
  }

  def testCV = {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

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
}
