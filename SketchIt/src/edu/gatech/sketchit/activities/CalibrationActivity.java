package edu.gatech.sketchit.activities;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class CalibrationActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";

    private Mat                  mRgba;
    private ColorDetector    	 mDetector;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

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
	private int currentFinger;
    public CalibrationActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	currentFinger = 0;
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
        mSpectrum = new Mat();
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }
    
    public boolean onTouch(View v, MotionEvent event) {
    	int cols = mRgba.cols();
        int rows = mRgba.rows();

        Rect centerRect = new Rect(cols/2-25,rows/2-35,50,70);

        Mat centerRectRgba = mRgba.submat(centerRect);

        Mat centerRectHsv = new Mat();
        Imgproc.cvtColor(centerRectRgba, centerRectHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        Scalar centerColorHsv = Core.sumElems(centerRectHsv);
        Log.d("yo",centerColorHsv.toString());
        int pointCount = centerRect.width*centerRect.height;
        for (int i = 0; i < centerColorHsv.val.length; i++)
        	centerColorHsv.val[i] /= pointCount;
        //Scalar centerColorRGB = convertScalarHsv2Rgba(centerColorHsv);

        mDetector = new ColorDetector(50*70,centerColorHsv);
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        int cols = mRgba.cols();
        int rows = mRgba.rows();

        Rect centerRect = new Rect(cols/2-25,rows/2-35,50,70);

        Core.rectangle(mRgba, centerRect.tl(), centerRect.br(), new Scalar(255, 255, 0), 2, 8, 0 );
        Core.putText(mRgba, "Place finger "+fingers[currentFinger]+" in rectangle.", new Point(10,60), 0/*font*/, 1, new Scalar(255, 0, 0, 255), 3);
        Core.putText(mRgba, "Touch anywhere to capture.",  new Point(30,rows-100), 0/*font*/, 1, new Scalar(255, 0, 0, 255), 3);

        if(mDetector!=null){
        	Imgproc.drawContours(mRgba, mDetector.getContours(mRgba), -1, CONTOUR_COLOR);
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
