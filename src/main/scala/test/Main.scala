package test

import services.TwitterService
import fetch.TwitterFetcher
import twitter4j.Status
import org.opencv.core.Mat
import org.opencv.highgui.Highgui
import org.opencv.core.Core

object Main {

  val MAX_ITER = 1000

  def main(args: Array[String]) {
    testCV
  }

  def testCV = {
    println(System.getProperties.getProperty("java.library.path"))
    println(Core.NATIVE_LIBRARY_NAME)
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    MatchingDemo.run
  }

  def testTw() = {
    val fetcher = new TwitterFetcher().connect(TwitterService).startAsyncFetch()

    (1 to MAX_ITER) foreach (_ => {
      val urls: Seq[String] = fetcher.consume(10).flatMap { x =>
        x match {
          case status: Status => status.getMediaEntities.map { x =>  x.getMediaURL}
          case _ => throw new ClassCastException
        }
      }

      urls.map(println)
    })
  }
}
