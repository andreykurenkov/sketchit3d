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
			coords[3*i] = (float)vertices[i].x;
			coords[3*i+1] = (float)vertices[i].y;
			coords[3*i+2] = (float)vertices[i].z;
		}
		setup();
	}
	
	@Override
	public void draw(float[] mvpMatrix) {
//		super.draw(mvpMatrix);
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
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
