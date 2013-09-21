package edu.gatech.sketchit.activities;

import org.opencv.core.Point3;

import edu.gatech.sketchit.AGLRenderer;
import edu.gatech.sketchit.shapes.Rectangle;
import edu.gatech.sketchit.shapes.Shape;
import edu.gatech.sketchit.shapes.Triangle;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

public class SketchActivity extends Activity{
	private GLSurfaceView mGLView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGLView = new MyGLSurfaceView(this);
		setContentView(mGLView);
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