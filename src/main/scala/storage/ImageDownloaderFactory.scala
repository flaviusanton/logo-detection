package storage

import scala.io.Source

object ImageDownloaderFactory {
  
  private val configFilename = "config/imageDownloader.config"
  
  private val downloader = {
    val config = loadConfig()
     
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
  
  def loadConfig(): Map[String, String] = {
    var map: Map[String, String] = Map()
    
    for (line <- Source.fromFile(configFilename).getLines()) {
      val arr : Array[String] = line.split("=")
      map += (arr(0) -> (if (arr.length > 1) arr(1) else ""))
    }
    map
  }
}