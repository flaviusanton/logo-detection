package services

import scala.io.Source
import scala.collection.immutable.Map

object TwitterService extends Service {

  def loadConfig(configFilename: String): Map[String, String] = {
    var map: Map[String, String] = Map()

    for (line <- Source.fromFile(configFilename).getLines()) {
      val arr : Array[String] = line.split("=")
      map += (arr(0) -> (if (arr.length > 1) arr(1) else ""))
    }
    map
  }

  def buildConfig(map: Map[String, String]): Any = {
    new twitter4j.conf.ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(map("consumerKey"))
      .setOAuthConsumerSecret(map("consumerSecret"))
      .setOAuthAccessToken(map("accessToken"))
      .setOAuthAccessTokenSecret(map("accessTokenSecret"))
  }

}
