package fetch

import services.Service

trait Fetcher {
  def connect(service: Service): Fetcher
  def startAsyncFetch(): Fetcher
  def consume(n: Integer): Seq[Any]
  def setLimit(limit: Integer): Fetcher
}
