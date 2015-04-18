package imgretrieve

import java.io.File
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import scala.util.Try
import core.Downloader

class DiskImageKeeper extends ImageKeeper {

  val PATH = "/tmp/tw-images"
  val imDir = new File(PATH)

  if (!imDir.exists())
    imDir.mkdir()

  def putImage(link: String, bytes: Array[Byte]): Try[File] = Try({
    val imName = link.substring(link.lastIndexOf("/") + 1)
    val imFile = new File(imDir + "/" + imName)
    val downloader = Downloader

    Try {
      val bf = new BufferedOutputStream(new FileOutputStream(imFile))
      bf.write(bytes)
      bf.close()
    }
    imFile
  });

  def getImage(link: String): Try[File] = Try({
    val imName = link.substring(link.lastIndexOf("/") + 1)
    val imFile = new File(imDir + "/" + imName)
    val downloader = Downloader

    if (!imFile.exists()) {
      val bytes = downloader.downloadImage(link)
      if (bytes.isSuccess) {
        val bf = new BufferedOutputStream(new FileOutputStream(imFile))
        bf.write(bytes.get)
        bf.close()
      }
    }
    imFile
  })
}