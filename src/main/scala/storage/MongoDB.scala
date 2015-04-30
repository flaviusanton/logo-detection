package storage

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.BasicDBObject

object MongoDB extends StorageService {
  private val mongoClient = MongoClient("Tiny", 27017)
  private val db = mongoClient("logodb")
  private val coll = db("logos")

  def load(imageLink: String): Option[Array[String]] = {
    println("TODO")
    None
  }

  def store(imageLink: String, annotations: Array[String]): Unit = {
    val doc = new BasicDBObject()
    doc.append("imageLink", imageLink)
    doc.append("annotations", annotations)

    coll.insert(doc)
  }
}