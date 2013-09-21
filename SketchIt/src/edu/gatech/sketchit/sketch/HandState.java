package edu.gatech.sketchit.sketch;

import android.graphics.Point;

public class HandState {
	public static Point a_coordinates = new Point(-1,-1);
	public static boolean a_clicked = false;
	public static Point b_coordinates = new Point(-1,-1);
	public static boolean b_clicked = false;
	
	public static boolean a_clicked(){
		return a_clicked;
	}
	
	public static boolean b_clicked(){
		return b_clicked;
	}
	
	public static Point a_coordinate(){
		  
		return new Point(0,0);
	}
}
