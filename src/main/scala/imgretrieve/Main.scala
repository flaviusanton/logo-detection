package imgretrieve

object Main {

  val INFIFO_PATH = "links.fifo"
  val OUTFIFO_PATH = "images.fifo"

  def main(args: Array[String]) {
    val downloader = new MemoryImageDownloader
    val producer = new PipeProducer(OUTFIFO_PATH)
    val consumer = new PipeConsumer(INFIFO_PATH, downloader, producer)
    
    consumer.run()
  }

}