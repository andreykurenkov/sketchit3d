package edu.gatech.sketchit.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import org.opencv.core.Point3;

import android.opengl.GLES20;

public class Rectangle extends Shape {
    private static final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    private final ShortBuffer drawListBuffer;
    
    /**
     * Pass in arguments counter-clockwise
     * @param a Top Left
     * @param b Bottom Left
     * @param c Bottom Right
     * @param d Top Right
     */
	public Rectangle(Point3 a, Point3 b, Point3 c, Point3 d) {
		coords = new float[12];
		Point3[] vertices = {a,b,c,d};
		for(int i=0;i<vertices.length;i++) {
			coords[i] = (float)vertices[i].x;
			coords[i+1] = (float)vertices[i].y;
			coords[i+2] = (float)vertices[i].z;
		}
		setup();
		ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);
	}
	
	@Override
	public void draw(float[] mvpMatrix) {
		super.draw(mvpMatrix);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, 
								GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}
