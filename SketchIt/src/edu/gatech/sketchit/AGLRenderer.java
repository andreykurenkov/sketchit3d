package edu.gatech.sketchit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.opencv.core.Point3;

import edu.gatech.sketchit.shapes.Rectangle;
import edu.gatech.sketchit.shapes.Shape;
import edu.gatech.sketchit.shapes.Triangle;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class AGLRenderer implements Renderer {
	protected static final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
		    "attribute vec4 vPosition;" +
		    "void main() {" +
		    "  gl_Position = uMVPMatrix * vPosition;" +
		    "  gl_PointSize = 999;" +
		    "}";
	protected static final String fragmentShaderCode =
		    "precision mediump float;" +
		    "uniform vec4 vColor;" +
		    "void main() {" +
		    "  gl_FragColor = vColor;" +
		    "}";
	
	protected static final int vertexShader = AGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    protected static final int fragmentShader = AGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
    
    public static final int COORDS_PER_VERTEX = 3;
    public static final int vertexStride = COORDS_PER_VERTEX * 4;
    
	public static int mProgram = GLES20.glCreateProgram();
	
    private List<Shape> shapes = new ArrayList<Shape>();
	
	private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
  
    
    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle = 0;
        
	@Override
	public void onDrawFrame(GL10 unused) {
		
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 1.5f, 0f, 0f, -5.0f, 0f, 1.0f, 0.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
//		Matrix.setIdentityM(mRotationMatrix, 0);
		float[] mRotationMatrix = new float[16];
		Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0.0f, 1);
		Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0,  mRotationMatrix, 0);

		//draw shapes here
//		myTriangle.draw(mMVPMatrix);
		for(int i=0;i<shapes.size();i++) {
			shapes.get(i).draw(mMVPMatrix);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float)width /height;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);
		GLES20.glClearColor(1.0f, .7f, .2f, 1.0f);
//		GLES20.glEnable(GLES20.GL_VERTEX_SHADER);
//		GLES20.glEnable(GLES20.GL_ALPHA);
//		GLES20.glEnable(GLES20.GL_BLEND);
////		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		Shape r = new Rectangle(new Point3(-100, -100, 0.0f),
				new Point3(-100, 100, 0.0f),
				new Point3(100, 100, 0.0f),
				new Point3(100, -100, 0.0f));
//		shapes.add(r);
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
	
	public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("AGLRenderer", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
	
}
