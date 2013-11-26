package com.cs371m.notesync;

public class Point {
	float x;
	float y;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static float getDistance(Point p1, Point p2)
	{
		return (float) Math.sqrt(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2));
	}
}
