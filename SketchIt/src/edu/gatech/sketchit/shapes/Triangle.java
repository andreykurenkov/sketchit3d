package edu.gatech.sketchit.shapes;

import org.opencv.core.Point3;

import android.opengl.GLES20;

public class Triangle extends Shape {
    
	public Triangle(Point3 a, Point3 b, Point3 c) {
		super(a, b, c);
	}

	@Override
	public void draw(float[] mvpMatrix) {
		super.draw(mvpMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	@Override
	public boolean contains(Point3 hand) {
		if(inLine(vertices[0], vertices[1], hand) || inLine(vertices[1], vertices[2], hand) || inLine(vertices[0], vertices[2], hand))
			return true;
		return false;
	}
}
