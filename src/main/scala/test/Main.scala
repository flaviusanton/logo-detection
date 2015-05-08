package test

import services.TwitterService
import fetch.TwitterFetcher
import twitter4j.Status
import org.opencv.core.Core
import imgdetect.LogoDetector

object Main {

  val MAX_ITER = 1000

  def main(args: Array[String]) {
    testCV
  }

  def testCV = {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    val detector = new TemplateMatcher("google.jpg")
    detector.detect("query.jpg")

    //val detector = BetterMatcher
    //detector.run("google.jpg", "query.jpg")
  }
}
