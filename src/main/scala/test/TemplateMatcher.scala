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

class TemplateMatcher(trainLogos: Array[String]) extends Detector {

  def detect(imageLink: String): Array[String] = {
    var arr = Array[String]()

    for (trainLogo <- trainLogos) {
      if (checkMatch(trainLogo, imageLink)) {
        arr :+= trainLogo
        return arr
      }
    }
    arr
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

    //trainImage.release()
    //Highgui.imwrite("upLeft.jpg", upLeft)
    //Highgui.imwrite("upRight.jpg", upRight)
    //Highgui.imwrite("downLeft.jpg", downLeft)
    //Highgui.imwrite("downRight.jpg", downRight)

    for (i <- (1 to 22)) {
      val queryClone = queryImage.clone()

      //println(i, upLeft.rows, upLeft.cols)
      if (upLeft.rows < 3 || upLeft.cols < 3)
        return false;

      val start = System.currentTimeMillis()
      val (mmrUpLeft, secondMmrUpLeft) = bestMatch(queryClone, upLeft, "out-final" + i + ".jpg", new Scalar(0, 0, 255))
      val (mmrUpRight, secondMmrUpRight) = bestMatch(queryClone, upRight, "out-final" + i + ".jpg", new Scalar(0, 255, 255))
      val (mmrDownLeft, secondMmrDownLeft) = bestMatch(queryClone, downLeft, "out-final" + i + ".jpg", new Scalar(255, 0, 0))
      val (mmrDownRight, secondMmrDownRight) = bestMatch(queryClone, downRight, "out-final" + i + ".jpg", new Scalar(0, 255, 0))

      val end = System.currentTimeMillis()
      //println("Time: " + (end - start))

      val haveAMatch = evaluateMatch(mmrUpLeft, mmrUpRight,
        mmrDownLeft, mmrDownRight, upLeft.rows, upLeft.cols,
        secondMmrUpLeft, secondMmrUpRight, secondMmrDownLeft, secondMmrDownRight)

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

  def bestMatch(queryImage: Mat, trainImage: Mat, outFile: String, color: Scalar): (MinMaxLocResult, MinMaxLocResult) = {

    val edgedQuery = queryImage
    val edgedTrain = trainImage

    //val result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
    val result = new Mat

    // / Do the Matching and Normalize
    Imgproc.matchTemplate(edgedQuery, edgedTrain, result, Imgproc.TM_CCOEFF_NORMED);
    //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

    // / Localizing the best match with minMaxLoc
    val mmr = Core.minMaxLoc(result);
    //println("Max: " + mmr.maxLoc + " " + mmr.maxVal)

    val matchLoc = mmr.maxLoc;
    //System.out.println(mmr.maxLoc, mmr.maxVal);

    // / Show me what you got
    Core.rectangle(edgedQuery, matchLoc, new Point(matchLoc.x + edgedTrain.cols(),
      matchLoc.y + edgedTrain.rows()), color, 1);

    Imgproc.floodFill(result, new Mat, matchLoc, new Scalar(0, 0, 0), new Rect, new Scalar(0.1), new Scalar(0.1), Imgproc.FLOODFILL_FIXED_RANGE)
    val mmr2 = Core.minMaxLoc(result);
    //println("Max: " + mmr2.maxLoc + " " + mmr2.maxVal)
    //println()

    return (mmr, mmr2)
  }

  def evaluateMatch(mmrul: MinMaxLocResult, mmrur: MinMaxLocResult,
                    mmrdl: MinMaxLocResult, mmrdr: MinMaxLocResult, rows: Int, cols: Int,
                    secondmmrul: MinMaxLocResult, secondmmrur: MinMaxLocResult,
                    secondmmrdl: MinMaxLocResult, secondmmrdr: MinMaxLocResult): Boolean = {

    val pul = mmrul.maxLoc
    val pur = mmrur.maxLoc
    val pdl = mmrdl.maxLoc
    val pdr = mmrdr.maxLoc

    val res1 = checkHorizontal(pul, pur, rows / 2, cols + (cols / 1.5).asInstanceOf[Int])
    val res2 = checkHorizontal(pdl, pdr, rows / 2, cols + (cols / 1.5).asInstanceOf[Int])
    val res3 = checkVertical(pul, pdl, cols / 2, rows + (rows / 1.5).asInstanceOf[Int])
    val res4 = checkVertical(pur, pdr, cols / 2, rows + (rows / 1.5).asInstanceOf[Int])

    //println(res1, res2, res3, res4)

    if (res1 && res2 && res3 && res4) {
      if (pul.y > pdr.y)
        return false
      if (pur.y > pdl.y)
        return false

      return true
    }

    if (res1 && res3) {
      val suspect = mmrdr
      if (suspect.maxVal < (mmrul.maxVal + mmrur.maxVal + mmrdl.maxVal) / 3)
        return true
      if (checkHorizontal(mmrdl.maxLoc, secondmmrdr.maxLoc, rows / 2, cols + (cols / 1.5).asInstanceOf[Int]))
        return true
    }

    if (res1 && res4) {
      val suspect = mmrdl
      if (suspect.maxVal < (mmrul.maxVal + mmrur.maxVal + mmrdr.maxVal) / 3)
        return true
      if (checkHorizontal(secondmmrdl.maxLoc, mmrdr.maxLoc, rows / 2, cols + (cols / 1.5).asInstanceOf[Int]))
        return true
    }

    if (res2 && res3) {
      val suspect = mmrur
      if (suspect.maxVal < (mmrul.maxVal + mmrdl.maxVal + mmrdr.maxVal) / 3)
        return true
      if (checkHorizontal(mmrul.maxLoc, secondmmrur.maxLoc, rows / 2, cols + (cols / 1.5).asInstanceOf[Int]))
        return true
    }

    if (res2 && res4) {
      val suspect = mmrul
      if (suspect.maxVal < (mmrur.maxVal + mmrdl.maxVal + mmrdr.maxVal) / 3)
        return true
      if (checkHorizontal(secondmmrul.maxLoc, mmrur.maxLoc, rows / 2, cols + (cols / 1.5).asInstanceOf[Int]))
        return true
    }
    return false
  }

  def checkVertical(up: Point, down: Point, centreThresh: Int, distThresh: Int): Boolean = {
    //println(up, down, centreThresh, distThresh)
    //println(up.x - down.x)
    if (Math.abs(up.x - down.x) > centreThresh)
      return false

    if (down.y < up.y + distThresh / 3)
      return false

    if (down.y - up.y > distThresh)
      return false

    //println(down.y - up.y)
    return true
  }

  def checkHorizontal(left: Point, right: Point, centreThresh: Int, distThresh: Int): Boolean = {
    if (Math.abs(right.y - left.y) > centreThresh)
      return false

    if (right.x < left.x + distThresh / 3)
      return false

    if (right.x - left.x > distThresh)
      return false

    return true
  }
}
