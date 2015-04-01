package services

import scala.io.Source
import scala.collection.immutable.Map

object TwitterService extends Service {

  def loadConfig(configFilename: String): Map[String, String] = {
    Source.fromFile(configFilename).getLines().map(_.split("=")).collect {
      case Array(key, value) => (key -> value)
    }.toMap
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
