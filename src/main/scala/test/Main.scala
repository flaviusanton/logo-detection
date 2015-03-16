package test

import services.TwitterService
import fetch.TwitterFetcher

object Main {
  
  def main(args: Array[String]) {
    new TwitterFetcher().connect(TwitterService)
  }
  
}