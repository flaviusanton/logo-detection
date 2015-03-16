package services

trait Service {
  def loadConfig(configFilename: String): Map[String, String]
  def buildConfig(map: Map[String, String]): Any
}