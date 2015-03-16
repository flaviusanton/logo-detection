package fetch

import services.Service

trait Fetcher {
  def connect(service: Service): Boolean
  def startAsyncFetch()
  def take(n: Integer)
  def setLimit(limit: Integer)
}
