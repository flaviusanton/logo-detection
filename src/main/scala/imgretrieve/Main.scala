package imgretrieve

object Main {

  val FIFO_PATH = "links.fifo"
  
  def main(args: Array[String]) {
    val consumer = new DummyConsumer(FIFO_PATH)
    
    consumer.run()
  }

}