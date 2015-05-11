package test

import imgdetect.Detector
import org.opencv.highgui.Highgui
import org.opencv.imgproc.Imgproc
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.core.Core
import org.opencv.core.Core.MinMaxLocResult
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.core.Rect
import java.io.File
import imgretrieve.DiskImageKeeper
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

class TemplateDetector(trainLogos: Array[String]) extends Detector {
  val LOGODIR = "/train-logos/"

  private val keeper = new DiskImageKeeper
  private val trainImgMap = new HashMap[String, Array[String]]

  trainLogos.foreach { logo =>
    val logoDir = new File(getClass.getResource(LOGODIR + logo).getPath)
    val logoImgs = logoDir.listFiles.map(_.getAbsolutePath)
    trainImgMap.put(logo, logoImgs)
  }

  def detect(imageLink: String): Array[String] = {
    val imFile = keeper.getImage(imageLink)
    val result = ArrayBuffer[String]()

    if (imFile.isSuccess) {
      trainLogos.foreach { logo =>
        val trainImgs = trainImgMap.get(logo)

        trainImgs match {
          case None => System.err.println(s"Cannot find logo in Logo Map.")
          case Some(x) => {
            val score = tryAllMatches(imFile.get.getAbsolutePath, x)
            println(s"Score with ${logo} for ${imageLink}: ${score}")
            if (score) {
              result.append(logo)
              //TODO: fix this
              return result.toArray
            }
          }
        }

      }
    } else {
      System.err.println(s"Failed to load image ${imageLink}")
    }

    return result.toArray
  }

  def tryAllMatches(imQuery: String, trainLogos: Array[String]): Boolean = {
    for (logo <- trainLogos) {
      //println(s"Trying ${imQuery} with: ${logo}")
      if (checkMatch(logo, imQuery))
        return true
    }
    return false
  }

  def checkMatch(trainLogo: String, imageLink: String): Boolean = {
    val queryImage = Highgui.imread(imageLink, Highgui.CV_LOAD_IMAGE_COLOR) //, Highgui.CV_LOAD_IMAGE_GRAYSCALE)
    val trainImage = Highgui.imread(trainLogo, Highgui.CV_LOAD_IMAGE_COLOR) //, Highgui.CV_LOAD_IMAGE_GRAYSCALE)

    val scaleX = queryImage.rows.asInstanceOf[Double] / trainImage.rows
    val scaleY = queryImage.cols.asInstanceOf[Double] / trainImage.cols
    val scale = if (scaleX < scaleY) scaleX else scaleY

    if (scale < 1) {
      Imgproc.resize(trainImage, trainImage, new Size(trainImage.cols * scale, trainImage.rows * scale))
    }

    val upLeft = trainImage.submat(0, trainImage.rows / 2, 0, trainImage.cols / 2)
    val upRight = trainImage.submat(0, trainImage.rows / 2, trainImage.cols / 2, trainImage.cols)
    val downLeft = trainImage.submat(trainImage.rows / 2, trainImage.rows, 0, trainImage.cols / 2)
    val downRight = trainImage.submat(trainImage.rows / 2, trainImage.rows, trainImage.cols / 2, trainImage.cols)

    for (i <- (1 to 22)) {
      val queryClone = queryImage.clone()

      //println(i, upLeft.rows, upLeft.cols)

      if (upLeft.rows < 3 || upLeft.cols < 3)
        return false

      val ful = Future[MinMaxLocResult] {
        bestMatch(queryClone, upLeft, "out-final" + i + ".jpg", new Scalar(0, 0, 255))
      }
      val fur = Future[MinMaxLocResult] {
        bestMatch(queryClone, upRight, "out-final" + i + ".jpg", new Scalar(0, 255, 255))
      }
      val fdl = Future[MinMaxLocResult] {
        bestMatch(queryClone, downLeft, "out-final" + i + ".jpg", new Scalar(255, 0, 0))
      }
      val fdr = Future[MinMaxLocResult] {
        bestMatch(queryClone, downRight, "out-final" + i + ".jpg", new Scalar(0, 255, 0))
      }

      import scala.concurrent.duration._

      val mmrUpLeft = Await.result(ful, 10.seconds)
      val mmrUpRight = Await.result(fur, 10.seconds)
      val mmrDownLeft = Await.result(fdl, 10.seconds)
      val mmrDownRight = Await.result(fdr, 10.seconds)

      val haveAMatch = evaluateMatch(mmrUpLeft, mmrUpRight,
        mmrDownLeft, mmrDownRight, upLeft.rows, upLeft.cols)

      Imgproc.resize(upLeft, upLeft, new Size(upLeft.cols / 1.17, upLeft.rows / 1.17))
      Imgproc.resize(upRight, upRight, new Size(upRight.cols / 1.17, upRight.rows / 1.17))
      Imgproc.resize(downLeft, downLeft, new Size(downLeft.cols / 1.17, downLeft.rows / 1.17))
      Imgproc.resize(downRight, downRight, new Size(downRight.cols / 1.17, downRight.rows / 1.17))

      Highgui.imwrite("out-" + i + ".jpg", queryClone);

      if (haveAMatch) {
        println("TRUE")
        upLeft.release()
        upRight.release()
        downLeft.release()
        downRight.release()
        queryImage.release()
        queryClone.release()

        return true
      }

      queryClone.release()
      System.gc()
    }

    upLeft.release()
    upRight.release()
    downLeft.release()
    downRight.release()
    queryImage.release()

    return false
  }

  def bestMatch(queryImage: Mat, trainImage: Mat, outFile: String, color: Scalar): MinMaxLocResult = {

    val edgedQuery = queryImage
    val edgedTrain = trainImage

    val result = new Mat

    Imgproc.matchTemplate(edgedQuery, edgedTrain, result, Imgproc.TM_CCOEFF_NORMED);

    val mmr = Core.minMaxLoc(result);
    val matchLoc = mmr.maxLoc;

    Core.rectangle(edgedQuery, matchLoc, new Point(matchLoc.x + edgedTrain.cols(),
      matchLoc.y + edgedTrain.rows()), color, 1);

    //Imgproc.floodFill(result, new Mat, matchLoc, new Scalar(0, 0, 0), new Rect, new Scalar(0.1), new Scalar(0.1), Imgproc.FLOODFILL_FIXED_RANGE)
    //val mmr2 = Core.minMaxLoc(result);

    return mmr
  }

  def evaluateMatch(mmrul: MinMaxLocResult, mmrur: MinMaxLocResult,
                    mmrdl: MinMaxLocResult, mmrdr: MinMaxLocResult, rows: Int, cols: Int): Boolean = {

    val pul = mmrul.maxLoc
    val pur = mmrur.maxLoc
    val pdl = mmrdl.maxLoc
    val pdr = mmrdr.maxLoc

    val res1 = checkHorizontal(pul, pur, rows / 2, cols + (cols / 1.5).asInstanceOf[Int])
    val res2 = checkHorizontal(pdl, pdr, rows / 2, cols + (cols / 1.5).asInstanceOf[Int])
    val res3 = checkVertical(pul, pdl, cols / 2, rows + (rows / 1.5).asInstanceOf[Int])
    val res4 = checkVertical(pur, pdr, cols / 2, rows + (rows / 1.5).asInstanceOf[Int])

    if (res1 && res2 && res3 && res4) {
      if (pul.y > pdr.y)
        return false
      if (pur.y > pdl.y)
        return false

      return true
    }

    return false
  }

  def checkVertical(up: Point, down: Point, centreThresh: Int, distThresh: Int): Boolean = {
    if (Math.abs(up.x - down.x) > centreThresh)
      return false

    if (down.y < up.y + distThresh / 2)
      return false

    if (down.y - up.y > distThresh)
      return false

    return true
  }

  def checkHorizontal(left: Point, right: Point, centreThresh: Int, distThresh: Int): Boolean = {
    if (Math.abs(right.y - left.y) > centreThresh)
      return false

    if (right.x < left.x + distThresh / 2)
      return false

    if (right.x - left.x > distThresh)
      return false

    return true
  }
}
