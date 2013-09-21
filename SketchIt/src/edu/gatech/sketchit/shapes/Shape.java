package edu.gatech.sketchit.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import edu.gatech.sketchit.AGLRenderer;
import android.opengl.GLES20;


public abstract class Shape {
	protected static final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
		    "attribute vec4 vPosition;" +
		    "void main() {" +
		    "  gl_Position = vPosition * uMVPMatrix;" +
		    "  gl_PointSize = 3;" +
		    "}";
	protected static final String fragmentShaderCode =
		    "precision mediump float;" +
		    "uniform vec4 vColor;" +
		    "void main() {" +
		    "  gl_FragColor = vColor;" +
		    "}";
	
	protected static final int vertexShader = AGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    protected static final int fragmentShader = AGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
    protected static final int COORDS_PER_VERTEX = 3;
    protected static final int vertexStride = COORDS_PER_VERTEX * 4;
    
    protected FloatBuffer vertexBuffer;
	protected int mProgram;
    protected int mPositionHandle;
    protected int mColorHandle;
    protected int mMVPMatrixHandle;
    
    protected int vertexCount;
	protected float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
    protected float[] coords;
    
    public Shape() {
    	mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(mProgram);
    }
    
    /**
     * Should be called by the sub-class's constructor
     */
    protected void setup() {
    	vertexCount = coords.length / COORDS_PER_VERTEX;
		ByteBuffer bb = ByteBuffer.allocateDirect(coords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(coords);
		vertexBuffer.position(0);
    }
    
    public void draw(float[] mvpMatrix) {
		GLES20.glUseProgram(mProgram);
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
		
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

		//sub class draws here
	}
}
