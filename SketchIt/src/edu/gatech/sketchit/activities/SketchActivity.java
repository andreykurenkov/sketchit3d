package edu.gatech.sketchit.activities;

import java.util.HashMap;

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
import edu.gatech.sketchit.shapes.Line;
import edu.gatech.sketchit.shapes.Rectangle;
import edu.gatech.sketchit.shapes.Shape;
import edu.gatech.sketchit.shapes.Triangle;
import edu.gatech.sketchit.sketch.Finger;
import edu.gatech.sketchit.sketch.Finger.finger_id;
import edu.gatech.sketchit.sketch.HandState;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class SketchActivity extends Activity implements CvCameraViewListener2{
	private GLSurfaceView mGLView;
	private static HashMap<finger_id, Finger> detectors;
	private static HandState rightHand, leftHand;
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
	public static void launch(Context by, HashMap<finger_id, Finger> hashMap){
		SketchActivity.detectors=hashMap;
		if(hashMap.containsKey(finger_id.Pointer_Left) && hashMap.containsKey(finger_id.Thumb_Left)){
			Finger leftMiddle = hashMap.containsKey(finger_id.Middle_Left)?hashMap.get(finger_id.Middle_Left):null;
			leftHand = new HandState(hashMap.get(finger_id.Pointer_Left),hashMap.get(finger_id.Thumb_Left),leftMiddle);
			Finger rightMiddle = hashMap.containsKey(finger_id.Middle_Right)?hashMap.get(finger_id.Middle_Right):null;
			rightHand = new HandState(hashMap.get(finger_id.Pointer_Right),hashMap.get(finger_id.Thumb_Right),rightMiddle);
		}
		by.startActivity(new Intent(by, SketchActivity.class));  
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gl_screen_view);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.gl_screen_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mGLView = new AGLSurfaceView(this);
		addContentView(mGLView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
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
		if(leftHand!=null){
			Point3 clicked = leftHand.updateClickedState(mRgba);
		}
		if(rightHand!=null){
			Point3 clicked = rightHand.updateClickedState(mRgba);
			if(clicked!=null){
				long downTime = rightHand.getTimeOfDown();
				//MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState)
			}
		}
		return mRgba;
	}
}

class AGLSurfaceView extends GLSurfaceView {
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;
	private float mPreviousZ;
	private final AGLRenderer mRenderer;
	private boolean generated;

	public AGLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mRenderer = new AGLRenderer();
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

	}

	private void generate() {
		for(int i=0;i<50;i++) {
			Point3[] rand = Shape.randomShape(2);
			Shape r = new Line(rand[0], rand[1]);
			mRenderer.addShape(r);
			requestRender();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if(!generated) {
			generated = true;
			generate();
		}

		float x = e.getX();
		float y = e.getY();

		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:

			float dx = x - mPreviousX;
			float dy = y - mPreviousY;

			// reverse direction of rotation above the mid-line
			if (y > getHeight() / 2) {
				dx = dx * -1 ;
			}

			// reverse direction of rotation to left of the mid-line
			if (x < getWidth() / 2) {
				dy = dy * -1 ;
			}

			mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
			requestRender();
		}

		mPreviousX = x;
		mPreviousY = y;
		return true;
	}	   
}