package imgretrieve

import java.io.File
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import scala.util.Try

class DiskImageKeeper extends ImageKeeper {

  val PATH = "/tmp/tw-images"
  val imDir = new File(PATH)

  if (!imDir.exists())
    imDir.mkdir()

  def putImage(link: String) = {
    val imName = link.substring(link.lastIndexOf("/") + 1)
    val imFile = new File(imDir + "/" + imName)

    if (!imFile.exists()) {
      val bytes = super.downloadImage(link)
      if (bytes.isSuccess) {
        Try {
          val bf = new BufferedOutputStream(new FileOutputStream(imFile))
          bf.write(bytes.get)
          bf.close()
        }
      }
    }
  }

  def getImage(link: String): Try[File] = Try({
    val imName = link.substring(link.lastIndexOf("/") + 1)
    val imFile = new File(imDir + "/" + imName)

    if (!imFile.exists()) {
      val bytes = super.downloadImage(link)
      if (bytes.isSuccess) {
        val bf = new BufferedOutputStream(new FileOutputStream(imFile))
        bf.write(bytes.get)
        bf.close()
      }
    }
    imFile
  })
}