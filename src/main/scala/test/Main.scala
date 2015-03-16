package test

import services.TwitterService
import fetch.TwitterFetcher
import twitter4j.Status

object Main {

  val MAX_ITER = 1000


  def main(args: Array[String]) {
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
