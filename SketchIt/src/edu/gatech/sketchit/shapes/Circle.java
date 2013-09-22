package edu.gatech.sketchit.shapes;

import org.opencv.core.Point3;

import android.opengl.GLES20;

public class Circle extends Shape {
	public int radius;
	public Circle(Point3 a, int radius) {
		super();
		this.radius = radius;
		Point3[] circ = new Point3[30];
		circ[0] = a;
		for(int i=1;i<circ.length;i++) {
			float percent =  i / (float)(circ.length - 1);
			float rad = (float) (percent * 2*Math.PI);
//			System.out.println()
			circ[i] = new Point3(a.x - radius*Math.cos(rad), a.y - radius*Math.sin(rad), a.z);
		}
		this.vertices = circ;
		loadBuffer();
		drawCode = GLES20.GL_TRIANGLE_FAN;
	}
	@Override
	public boolean contains(Point3 hand) {
		// TODO Auto-generated method stub
		return false;
	}
}
