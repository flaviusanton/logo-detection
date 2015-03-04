#include <iostream>
#include <stdio.h>
#include "opencv2/core/core.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/nonfree/nonfree.hpp"

using namespace cv;
using namespace std;

static void help()
{
    printf("Usage: ./matcher_simple <image1> <image2>\n");
}

int main(int argc, char** argv)
{
    if(argc != 3)
    {
        help();
        return -1;
    }

    Mat img1 = imread(argv[1], CV_LOAD_IMAGE_COLOR);
    Mat img2 = imread(argv[2], CV_LOAD_IMAGE_COLOR);

    if(img1.empty() || img2.empty())
    {
        printf("Can't read one of the images\n");
        return -1;
    }

    // detecting keypoints
    SurfFeatureDetector detector(800);
    vector<KeyPoint> keypoints1, keypoints2;
    detector.detect(img1, keypoints1);
    detector.detect(img2, keypoints2);

    // computing descriptors
    SurfDescriptorExtractor extractor;
    Mat descriptors1, descriptors2;
    extractor.compute(img1, keypoints1, descriptors1);
    extractor.compute(img2, keypoints2, descriptors2);

    // matching descriptors
    BFMatcher matcher(NORM_L2);
    vector<DMatch> matches;
    matcher.match(descriptors1, descriptors2, matches);

    // distance stuff
    double max_dist = 0, min_dist = 100;

    for (int i = 0; i < descriptors1.rows; ++i) {
	    double dist = matches[i].distance;
	    if (dist < min_dist)
		    min_dist = dist;
	    if (dist > max_dist)
		    max_dist = dist;
    }

    vector<DMatch> good_matches;
    for (int i = 0; i < descriptors1.rows; ++i) {
	    if (matches[i].distance < 3 * min_dist) {
		    good_matches.push_back(matches[i]);
	    }
    }
    // drawing the good matches
    namedWindow("matches-good", 1);
    Mat img_matches;
    drawMatches(img1, keypoints1, img2, keypoints2, good_matches, img_matches);
    imshow("matches-good", img_matches);

    // drawing the results
    namedWindow("matches-before", 1);
    img_matches;
    drawMatches(img1, keypoints1, img2, keypoints2, matches, img_matches);
    imshow("matches-before", img_matches);


    // remove matches that map to the same point too much
    vector<DMatch> clean_matches;

    for (int i = 0; i < matches.size(); ++i) {
	    Point2f p1, p2;
	    p1 = keypoints1[matches[i].queryIdx].pt;
	    p2 = keypoints2[matches[i].trainIdx].pt;

	    int n_p2 = 0;
	    for (int j = 0; j < matches.size(); ++j) {
		    if (keypoints2[matches[j].trainIdx].pt == p2)
			    n_p2++;
	    }

	    cout << p2 << " " << n_p2 << endl;

	    if (n_p2 < 3)
		    clean_matches.push_back(matches[i]);
    }

    matches = clean_matches;

    // drawing the results
    namedWindow("matches-after", 1);
    drawMatches(img1, keypoints1, img2, keypoints2, matches, img_matches);
    imshow("matches-after", img_matches);

    int positives = 0;
    int negatives = 0;
    int zeroish = 0;

    for (int i = 0; i < matches.size(); ++i) {
	    Point2f p1, p2;
	    p1 = keypoints1[matches[i].queryIdx].pt;
	    p2 = keypoints2[matches[i].trainIdx].pt;

	    float m;
	    if (p1.x == p2.x)
		    m = 10000000000;
	    else {
		    m = (p2.y - p1.y) / (p2.x - p1.x);
	    }

	    cout << p1 << " " << p2 << " " << p1 - p2 << "   " << m << endl;
	    if (m >= 1)
		    positives++;
	    else if (m <= -1)
		    negatives++;
	    else
		    zeroish++;
    }

    cout << "Pos: " << positives << "  Neg: " << negatives << endl;
    cout << "Zeroish: " << zeroish << endl;

    // TODO
    /*
    vector<Point2f> obj;
    vector<Point2f> scene;
    for (int i = 0; i < matches.size(); ++i) {
	    obj.push_back(keypoints1[matches[i].queryIdx].pt;
	    scene.push_back(keypoints2[matches[i].trainIdx].pt;
    }

    Mat H = findHomography(obj, scene, CV_RANSAC);

    */

    waitKey(0);
    return 0;
}
