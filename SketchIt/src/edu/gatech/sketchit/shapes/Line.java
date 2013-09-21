package edu.gatech.sketchit.shapes;

import org.opencv.core.Point3;

import android.opengl.GLES20;

public class Line extends Shape {
	public Line(Point3 a, Point3 b) {
		super(a, b);
	}
	
	@Override
	public void draw(float[] mvpMatrix) {
		super.draw(mvpMatrix);
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
