package storage

import scala.collection.mutable.HashMap

object MemDB extends StorageService {

  val map = new HashMap[String, Array[String]]

  def load(imageLink: String): Option[Array[String]] = {
    map.get(imageLink)
  }

  def store(imageLink: String, annotations: Array[String]): Unit = {
    print(s"Storing ${imageLink} : ")
    annotations.map { x => print(x + ", ") }
    println()

    map.put(imageLink, annotations)
  }
}