package imgretrieve

object Main {

  val FIFO_PATH = "links.fifo"

  def main(args: Array[String]) {
    val consumer = new MemoryImageDownloader(FIFO_PATH)
    
    consumer.run()
  }

}