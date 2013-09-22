package edu.gatech.sketchit.cv;


import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.graphics.Color;

public class ColorDetector {
	// Lower and Upper bounds for range checking in HSV color space
	private Scalar lowerBound;
	private Scalar upperBound;
	//More efficient to just create here
	private Mat spectrum;
	private double originalArea;

	public static Scalar getHSVScalar(int red, int green, int blue){
		float[] hsv = new float[3];
		Color.RGBToHSV(red, green, blue, hsv);
		return new Scalar((int)hsv[0],(int)hsv[1],(int)hsv[2]);
	}

	public ColorDetector(double originalArea, int red, int green, int blue){
		this(originalArea, getHSVScalar(red,green,blue));
	}

	public ColorDetector(double originalArea, Scalar hsvColor){
		this(originalArea, hsvColor,new Scalar(15,30,25,0));
	}

	public ColorDetector(double originalArea, Scalar hsvColor, Scalar colorRadius){
		this.originalArea = originalArea;
		double minH = (hsvColor.val[0] >= colorRadius.val[0]) ? hsvColor.val[0]-colorRadius.val[0] : 0;
		double maxH = (hsvColor.val[0]+colorRadius.val[0] <= 255) ? hsvColor.val[0]+colorRadius.val[0] : 255;

		lowerBound = new Scalar(minH,hsvColor.val[1] - colorRadius.val[1],hsvColor.val[2] - colorRadius.val[2],0);
		upperBound = new Scalar(maxH,hsvColor.val[1] + colorRadius.val[1],hsvColor.val[2] + colorRadius.val[2],255);

		Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

		for (int j = 0; j < maxH-minH; j++) {
			byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
			spectrumHsv.put(0, j, tmp);
		}
		spectrum = new Mat();
		Imgproc.cvtColor(spectrumHsv, spectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
	}

	// Cache
	Mat mPyrDownMat = new Mat();
	Mat mHsvMat = new Mat();
	Mat mMask = new Mat();
	Mat mDilatedMask = new Mat();
	Mat mHierarchy = new Mat();

	public Mat getSpectrum() {
		return spectrum;
	}

	public MatOfPoint getBiggestContour(Mat rgbaImage) {
		return getBiggestContour(getContours(rgbaImage));
	}
	
	public List<MatOfPoint> getContours(Mat rgbaImage) {
		return this.getContours(rgbaImage,0.2);
	}
	
	public List<MatOfPoint> getContours(Mat rgbaImage,double minContourArea ) {
		Imgproc.pyrDown(rgbaImage, mPyrDownMat);
		Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

		Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

		Core.inRange(mHsvMat, lowerBound, upperBound, mMask);
		Imgproc.dilate(mMask, mDilatedMask, new Mat());

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Find max contour area
		double maxArea = 0;
		for(MatOfPoint contour: contours){
			double area = Imgproc.contourArea(contour);
			if (area > maxArea)
				maxArea = area;
		}

		// Filter contours by area and resize to fit the original image size
		ArrayList<MatOfPoint> toReturn = new ArrayList<MatOfPoint>();
		for(MatOfPoint contour:contours){
			if (Imgproc.contourArea(contour) > minContourArea*maxArea) {
				Core.multiply(contour, new Scalar(4,4), contour);
				toReturn.add(contour);
			}
		}
		return toReturn;
	}

	public Point3 detectBiggestBlob(Mat image){
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		MatOfPoint biggest = getBiggestContour(contours);
		Rect boundRect = Imgproc.boundingRect(biggest);
		return getPoint3(boundRect);
	}
	
	public static MatOfPoint getBiggestContour(List<MatOfPoint> contours){
		double maxArea = 0;
		MatOfPoint biggest = null;
		for(MatOfPoint wrapper: contours){
			double area = Imgproc.contourArea(wrapper);
			if (area > maxArea){
				maxArea = area;
				biggest = wrapper;
			}
		}
		return biggest;
	}

	public static Point getCenterPoint(Rect rect){
		Point tl= rect.tl();
		Point br= rect.br();
		return new Point((tl.x + br.x)/2,(tl.y + br.y)/2);
	}
	
	public Point3 getPoint3(Rect rect){
		Point tl= rect.tl();
		Point br= rect.br();
		return new Point3((tl.x + br.x)/2,(tl.y + br.y)/2,rect.area()/originalArea);
	}

	public static double getCountourArea(MatOfPoint contour){
		return Imgproc.contourArea(contour);
	}
	
}

