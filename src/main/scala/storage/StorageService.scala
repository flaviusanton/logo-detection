package storage

trait StorageService {
  def store(imageLink: String, annotations: Array[String])

  def load(imageLink: String): Option[Array[String]]
}