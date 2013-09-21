package edu.gatech.sketchit.activities;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;
import org.opencv.core.Point3;

import edu.gatech.sketchit.AGLRenderer;
import edu.gatech.sketchit.R;
import edu.gatech.sketchit.cv.ColorDetector;
import edu.gatech.sketchit.shapes.Rectangle;
import edu.gatech.sketchit.shapes.Shape;
import edu.gatech.sketchit.shapes.Triangle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class SketchActivity extends Activity implements CvCameraViewListener2{
	private static ColorDetector[] detectors;

	private CameraBridgeViewBase mOpenCvCameraView;

	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i("SketchActivity", "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};

	public static void launch(Context by, ColorDetector[] detectors){
		SketchActivity.detectors=detectors;
		by.startActivity(new Intent(by, SketchActivity.class));  
	}

	private GLSurfaceView mGLView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.full_screen_view);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.full_screen_view);
		mOpenCvCameraView.setCvCameraViewListener(this);


		mGLView = new MyGLSurfaceView(this);
		setContentView(mGLView);

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

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat mRgba = inputFrame.rgba();
		Log.i("bro","sup");
		//for(ColorDetector detector:detectors){
		//	Point3 point = detector.detectBiggestBlob(mRgba);
		//}
		return null;
	}
}

class MyGLSurfaceView extends GLSurfaceView {
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;
	private float mPreviousZ;
	private final AGLRenderer mRenderer;

	public MyGLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mRenderer = new AGLRenderer();
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}


	@Override
	public boolean onTouchEvent(MotionEvent e) {
		System.out.println("Creating a new rectangle...");
		Shape r = new Triangle(new Point3(-.5f, .4f, 0f),
				new Point3(-0.5f, -0.3f, 0f),
				new Point3(0.5f, -0.7f, 0f));
		//				new Point3(0.5f, 0.7f, 0));

		mRenderer.addShape(r);
		mRenderer.update();
		return true;
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		//        float x = e.getX();
		//        float y = e.getY();
		//
		//        switch (e.getAction()) {
		//            case MotionEvent.ACTION_MOVE:
		//
		//                float dx = x - mPreviousX;
		//                float dy = y - mPreviousY;
		//
		//                // reverse direction of rotation above the mid-line
		//                if (y > getHeight() / 2) {
		//                  dx = dx * -1 ;
		//                }
		//
		//                // reverse direction of rotation to left of the mid-line
		//                if (x < getWidth() / 2) {
		//                  dy = dy * -1 ;
		//                }
		//
		//                mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
		//	            requestRender();
		//	        }
		//
		//	        mPreviousX = x;
		//	        mPreviousY = y;
		//	        return true;
	}

}