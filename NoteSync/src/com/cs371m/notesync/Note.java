package com.cs371m.notesync;

import java.io.Serializable;
import java.util.ArrayList;


public class Note implements Serializable
{
	public String course;
	public String topic;
	public String image;
	public String recording;
	public ArrayList<Long> timestamps;
	public ArrayList<Point> bookmarks;

}
