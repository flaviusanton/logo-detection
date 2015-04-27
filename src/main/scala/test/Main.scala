package test

import services.TwitterService
import fetch.TwitterFetcher
import twitter4j.Status
import org.opencv.core.Core
import imgdetect.LogoDetector

object Main {

  val MAX_ITER = 1000

  def main(args: Array[String]) {
    println("TODO")
    testCV
  }

  def testCV = {
    println(System.getProperties.getProperty("java.library.path"))
    println(Core.NATIVE_LIBRARY_NAME)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    val detector = new LogoDetector(Array("google"))
    detector.detect("http:/gjhgh/google3.jpeg")
  }
}
