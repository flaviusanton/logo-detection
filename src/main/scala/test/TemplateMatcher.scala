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

class TemplateMatcher(trainLogo: String) extends Detector {

  def detect(imageLink: String): Array[String] = {
    val queryImage = Highgui.imread(imageLink, Highgui.CV_LOAD_IMAGE_UNCHANGED) //, Highgui.CV_LOAD_IMAGE_GRAYSCALE)
    val trainImage = Highgui.imread(trainLogo, Highgui.CV_LOAD_IMAGE_UNCHANGED) //, Highgui.CV_LOAD_IMAGE_GRAYSCALE)

    Imgproc.resize(trainImage, trainImage, new Size(trainImage.rows / 2, trainImage.cols / 2))

    val upLeft = trainImage.submat(0, trainImage.rows / 2, 0, trainImage.cols / 2)
    val upRight = trainImage.submat(0, trainImage.rows / 2, trainImage.cols / 2, trainImage.cols)
    val downLeft = trainImage.submat(trainImage.rows / 2, trainImage.rows, 0, trainImage.cols / 2)
    val downRight = trainImage.submat(trainImage.rows / 2, trainImage.rows, trainImage.cols / 2, trainImage.cols)

    for (i <- (1 to 17)) {
      val edgedQuery = queryImage.clone()

      Imgproc.resize(upLeft, upLeft, new Size(upLeft.rows / 1.2, upLeft.cols / 1.2))
      detect2(edgedQuery, upLeft, "out-final" + i + ".jpg")
      Imgproc.resize(upRight, upRight, new Size(upRight.rows / 1.2, upRight.cols / 1.2))
      detect2(edgedQuery, upRight, "out-final" + i + ".jpg")
      Imgproc.resize(downLeft, downLeft, new Size(downLeft.rows / 1.2, downLeft.cols / 1.2))
      detect2(edgedQuery, downLeft, "out-final" + i + ".jpg")
      Imgproc.resize(downRight, downRight, new Size(downRight.rows / 1.2, downRight.cols / 1.2))
      detect2(edgedQuery, downRight, "out-final" + i + ".jpg")

      Highgui.imwrite("out-final" + i + ".jpg", edgedQuery);
    }

    Array("Google")
  }

  def detect2(queryImage: Mat, trainImage: Mat, outFile: String): Array[String] = {

    /*val queryBlur = new Mat
    Imgproc.GaussianBlur(queryImage, queryBlur, new Size(5, 5), 0)

    val trainBlur = new Mat
    Imgproc.GaussianBlur(trainImage, trainBlur, new Size(5, 5), 0)

    val edgedQuery = new Mat
    Imgproc.Canny(queryBlur, edgedQuery, 50, 150)

    val edgedTrain = new Mat
    Imgproc.Canny(trainBlur, edgedTrain, 50, 150)*/

    //Highgui.imwrite("query-edged.bmp", edgedQuery)
    //Highgui.imwrite("train-edged.bmp", edgedTrain)

    val edgedQuery = queryImage
    val edgedTrain = trainImage
    val result_cols = edgedQuery.cols() - edgedTrain.cols() + 1;
    val result_rows = edgedQuery.rows() - edgedTrain.rows() + 1;

    //val result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
    val result = new Mat

    // / Do the Matching and Normalize
    Imgproc.matchTemplate(edgedQuery, edgedTrain, result, Imgproc.TM_CCORR_NORMED);
    //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

    // / Localizing the best match with minMaxLoc
    val mmr = Core.minMaxLoc(result);

    val matchLoc = mmr.maxLoc;
    System.out.println(mmr.maxLoc, mmr.maxVal);

    // / Show me what you got
    Core.rectangle(edgedQuery, matchLoc, new Point(matchLoc.x + edgedTrain.cols(),
      matchLoc.y + edgedTrain.rows()), new Scalar(255, 255, 0), 4);

    // Save the visualized detection.

    Array("Google")
  }
}