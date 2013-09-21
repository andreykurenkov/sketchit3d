package edu.gatech.sketchit.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import org.opencv.core.Point3;

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
	protected static int mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
    
    protected int mPositionHandle;
    protected int mColorHandle;
    protected int mMVPMatrixHandle;
    
    protected int vertexCount;
	protected float[] color = {0.0f, 0.0f, 0.0f, 1.0f};
    protected float[] coords;
    
    static {
    	GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);
    }
    
    public Shape(Point3... vertices) {
    	coords = new float[COORDS_PER_VERTEX*vertices.length];
		for(int i=0;i<vertices.length;i++) {
			coords[3*i] = (float)vertices[i].x;
			coords[3*i+1] = (float)vertices[i].y;
			coords[3*i+2] = (float)vertices[i].z;
		}

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
    	setup();
    	
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
    
    public static Point3[] randomShape(int numVertices){
    	Point3[] vertices = new Point3[numVertices];
		Random rand = new Random();
		for(int i=0;i<numVertices;i++) {
			Point3 p = new Point3();
			p.x = rand.nextFloat()*(rand.nextBoolean()?-1:1);
			p.y = rand.nextFloat()*(rand.nextBoolean()?-1:1);
			p.z = rand.nextFloat()*(rand.nextBoolean()?-1:1);
			vertices[i] = p;
		}
		return vertices;
    }
}
