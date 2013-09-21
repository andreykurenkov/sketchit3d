package edu.gatech.sketchit.shapes;

import org.opencv.core.Point3;

import android.opengl.GLES20;

public class Triangle extends Shape {
    
    /**
     * Pass in arguments counter-clockwise
     * @param a Top Left
     * @param b Bottom Left
     * @param c Bottom Right
     * @param d Top Right
     */
	public Triangle(Point3 a, Point3 b, Point3 c) {
		coords = new float[9];
		Point3[] vertices = {a,b,c};
		for(int i=0;i<vertices.length;i++) {
			coords[i] = (float)vertices[i].x;
			coords[i+1] = (float)vertices[i].y;
			coords[i+2] = (float)vertices[i].z;
		}
		setup();
	}
	
	@Override
	public void draw(float[] mvpMatrix) {
		super.draw(mvpMatrix);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
