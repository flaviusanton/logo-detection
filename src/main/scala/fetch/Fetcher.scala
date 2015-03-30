package fetch

import services.Service

trait Fetcher {
  
  val CHUNK_SIZE = 10
  
  def connect(service: Service): Fetcher
  def startAsyncFetch(): Fetcher
  def consume(n: Integer): Array[Object]
}
