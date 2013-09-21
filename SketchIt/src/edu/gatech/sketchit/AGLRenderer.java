package edu.gatech.sketchit;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.gatech.sketchit.shapes.Shape;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class AGLRenderer implements Renderer {
	
	private List<Shape> shapes;
	
	private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    
    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;
    
    
	@Override
	public void onDrawFrame(GL10 unused) {
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);	
		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, -1.0f, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0,  mMVPMatrix, 0);

		//draw shapes here
//		myTriangle.draw(mMVPMatrix);
		for(Shape shape : shapes) {
			shape.draw(mMVPMatrix);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio =  (float)width /height;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		shapes = new ArrayList<Shape>();
	}
	
	public static int loadShader(int type, String shaderCode){
	    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
	    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
	    int shader = GLES20.glCreateShader(type);
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);
	    return shader;
	}
	
	public boolean addShape(Shape s) {
		return shapes.add(s);
	}
	public boolean removeShape(Shape s) {
		return shapes.remove(s);
	}
	public void update() {
		
	}
}
