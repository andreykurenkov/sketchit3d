package edu.gatech.sketchit.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import org.opencv.core.Point3;

import android.opengl.GLES20;

public class Rectangle extends Shape {
    private static final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
    private ShortBuffer drawListBuffer;

	public Rectangle(Point3 a, Point3 b, Point3 c, Point3 d) {
		super(a,b,c,d);
	}

	@Override
	public void setup() {
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

	@Override
	public boolean contains(Point3 hand) {
		if(inLine(vertices[0], vertices[1], hand) || inLine(vertices[1], vertices[2], hand) || inLine(vertices[2], vertices[3], hand) || inLine(vertices[0], vertices[3], hand))
			return true;
		return false;
	}
}
