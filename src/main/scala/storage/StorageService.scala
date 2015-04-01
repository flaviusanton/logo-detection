package storage

trait StorageService {
  def store(imageLink: String, image: Array[Byte])
  
  def load(imageLink: String): Array[Byte]
}