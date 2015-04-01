package storage

import scala.io.Source

// no need to call this factory it only creates a single object
// A factory would have params
object ImageDownloaderFactory {
  
  private val configFilename = "config/imageDownloader.config"
  
  private val downloader = {
    val config = loadConfig(configFilename)
      // you can use Option class to avoid this kind of code
      if (config("class") == "DiskImageDownloader")
        new DiskImageDownloader(config("downloadPath"))
      else
        Unit
  } 
  
  def getDownloader(): ImageDownloader = {
    return downloader match {
      case diskImageDownloader: DiskImageDownloader => diskImageDownloader
      case _ => throw new ClassNotFoundException
    }
  }
  // let's avoid using vars if we can :D
  def loadConfig(configFilename: String): Map[String, String] = {
    Source.fromFile(configFilename).getLines().map(_.split("=")).collect {
      case Array(key, value) => (key -> value)
    }.toMap
  }
}