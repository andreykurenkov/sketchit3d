package edu.gatech.sketchit.shapes;

import org.opencv.core.Point3;

import android.opengl.GLES20;

public class Triangle extends Shape {
    
	public Triangle(Point3 a, Point3 b, Point3 c) {
		super(a, b, c);
		drawCode = GLES20.GL_TRIANGLES;
	}

//	@Override
//	public void draw(float[] mvpMatrix) {
//		super.draw(mvpMatrix);
//		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
//		// Disable vertex array
//		GLES20.glDisableVertexAttribArray(mPositionHandle);
//	}
}
