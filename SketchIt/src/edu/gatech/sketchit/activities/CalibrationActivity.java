package edu.gatech.sketchit.activities;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import edu.gatech.sketchit.R;
import edu.gatech.sketchit.cv.ColorDetector;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class CalibrationActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "Calibration Activity";

    private SharedPreferences prefs;    
    private Mat                  mRgba;
    private ColorDetector    	 detector;
    private Scalar centerColorHsv;
    
    private Size                 SPECTRUM_SIZE;
    private boolean touched;
    private boolean bad;
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CalibrationActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

	private String[] fingers={"middle (left hand)","pointing (left hand)", "thumb (left hand)",
									"middle (right hand)","pointing (right hand)", "thumb (right hand)"};
	private Mat[]                  spectrums;
	private int currentFinger;
	
    public CalibrationActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	currentFinger = 0;
    	bad = false;
    	touched = false;
       	prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.full_screen_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.full_screen_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        spectrums= new Mat[fingers.length];
        SPECTRUM_SIZE = new Size(200, 64);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }
    
    public boolean onTouch(View v, MotionEvent event) {
    	int cols = mRgba.cols();
        int rows = mRgba.rows();
        
        if(currentFinger<fingers.length){
	        Rect centerRect = new Rect(cols/2-25,rows/2-35,50,70);
	        
	        Mat centerRectRgba = mRgba.submat(centerRect);
	        Mat centerRectHsv = new Mat();
	        Imgproc.cvtColor(centerRectRgba, centerRectHsv, Imgproc.COLOR_RGB2HSV_FULL);
	
	        // Calculate average color of touched region
	        centerColorHsv = Core.sumElems(centerRectHsv);
	        int pointCount = centerRect.width*centerRect.height;
	        for (int i = 0; i < centerColorHsv.val.length; i++)
	        	centerColorHsv.val[i] /= pointCount;
	        //Scalar centerColorRGB = convertScalarHsv2Rgba(centerColorHsv);
	
	        detector = new ColorDetector(50*70,centerColorHsv);
	        touched = true;
	        bad = false;
        }
        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        int cols = mRgba.cols();
        int rows = mRgba.rows();

        Rect centerRect = new Rect(cols/2-25,rows/2-35,50,70);

        Core.rectangle(mRgba, centerRect.tl(), centerRect.br(), new Scalar(255, 255, 0), 2, 8, 0 );
        if(currentFinger<fingers.length){
	        Core.putText(mRgba, "Place "+fingers[currentFinger]+" finger in rectangle.", new Point(5,45), 0/*font*/, 1, new Scalar(255, 255, 255, 255), 3);
	        Core.putText(mRgba, "Touch anywhere to capture.",  new Point(5,rows-45), 0/*font*/, 1, new Scalar(255, 255, 255, 255), 3);
	
	        for(int i=0;i<currentFinger;i++){
	        	//Magic offsets!
		        Mat spectrumLabel = mRgba.submat(5+60*i, 5+60*i+ spectrums[i].rows(), cols-205, cols-205 + spectrums[i].cols());
	        	spectrums[i].copyTo(spectrumLabel);
		        Core.putText(mRgba, (i+1)+")"+fingers[i]+":", new Point(cols-540,45+60*i), 0/*font*/, 1, new Scalar(255, 255, 255, 125), 3);
	        }
	        
	        if(touched && detector!=null){
	            List<MatOfPoint> contours = detector.getContours(mRgba);
	            int area = 0;
	            for(MatOfPoint contour: contours){
	            	area += Imgproc.contourArea(contour);
	            }
	            if(area>10000 || area<1500){
	            	bad = true;
	            	Imgproc.drawContours(mRgba, detector.getContours(mRgba), -1, new Scalar(255,0,0,255));
	            }else{
	            	bad = false;
	            	spectrums[currentFinger] = new Mat();
			        Imgproc.resize(detector.getSpectrum(), spectrums[currentFinger], SPECTRUM_SIZE);
			        //TODO: more checking, see that blob in center
	            	prefs.edit().putString(fingers[currentFinger], centerColorHsv.val[0]+" "+centerColorHsv.val[1]+" "+centerColorHsv.val[2]).apply();
	            	currentFinger++;
	            	prefs.edit().putInt("current finger", currentFinger).apply();
	            	Imgproc.drawContours(mRgba, detector.getContours(mRgba), -1, new Scalar(0,255,0));
	            }
	            touched = false;
	        }
	        if(bad){
	            Core.putText(mRgba, "Bad background/finger coloring, try again.", new Point(5,100), 0/*font*/, 1, new Scalar(255,0,0,255), 3);
	        }
        }else{
        	Core.putText(mRgba, "Done!", new Point(5,45), 0/*font*/, 1, new Scalar(255, 255, 255, 255), 3);
        }

        return mRgba;
    }

    @SuppressWarnings("unused")
	private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}
