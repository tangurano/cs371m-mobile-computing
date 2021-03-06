package com.cs371m.notesync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class Helper {
	
	private static String filename = "NoteSyncNotes.xml";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_AUDIO = 2;
	
	public static Uri getOutputMediaFileUri(int type, Context c){
	      return Uri.fromFile(getOutputMediaFile(type, c));
	}

	
	public static File getOutputMediaFile(int type, Context c){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    //File mediaStorageDir = new File(c.getApplicationContext().getFilesDir().getAbsolutePath());
		// This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
		
		File mediaStorageDir  = new File(c.getApplicationContext().getExternalFilesDir(null).getAbsolutePath()); 
			//storage/sdcard0/Android/data/com.cs371m.notesync/files
			//external seems to work

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_AUDIO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".3gp");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	public static ArrayList<Note> loadNotes(Context c) throws XmlPullParserException, IOException
	{
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		ArrayList<Note> notes = new ArrayList<Note>();
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if (!(mExternalStorageAvailable && mExternalStorageWriteable))
			Log.w("NOTES IO ERROR", "storage unavailable or unwriteable!!!");
		
		FileInputStream fis = c.openFileInput(filename);
		StringBuilder sb = new StringBuilder();
		
		try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            fis.close();
        } catch(OutOfMemoryError om){
            om.printStackTrace();
        } catch(Exception ex){
            ex.printStackTrace();
        }
		
        String result = sb.toString();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        
        String currentTag = null;

        xpp.setInput(new StringReader(result));
        int eventType = xpp.getEventType();
        Note current = new Note();
        float currx = 0;
        float curry = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {
//        	if(eventType == XmlPullParser.START_DOCUMENT) {
//        		System.out.println("Start document");
        	if(eventType == XmlPullParser.START_TAG) {
        		currentTag = xpp.getName();
        		if (currentTag.equals("Note")) {
        			current = new Note();
        			notes.add(current);
        		} else if (currentTag.equals("course")) {
        			current.course = xpp.nextText();
        		} else if (currentTag.equals("topic")) {
        			current.topic = xpp.nextText();
        		}else if (currentTag.equals("date")) {
        			current.date = xpp.nextText();
        		} else if (currentTag.equals("recording")) {
        			current.recording = xpp.nextText();
        		} else if (currentTag.equals("image")) {
        			current.image = xpp.nextText();
        		} else if (currentTag.equals("timestamps")) {
        			current.timestamps = new ArrayList<Long>();
        		} else if (currentTag.equals("timestamp")) {
        			current.timestamps.add(Long.parseLong(xpp.nextText()));
        		} else if (currentTag.equals("bookmarks")) {
        			current.bookmarks = new ArrayList<Point>();
        		} else if (currentTag.equals("x")) {
        			currx = Float.parseFloat(xpp.nextText());
        		} else if (currentTag.equals("y")) {
        			curry = Float.parseFloat(xpp.nextText());
        			current.bookmarks.add(new Point(currx, curry));
        		}
        	}
        	eventType = xpp.next();
        }
        
		return notes;
	}
	
	public static void writeNotes(ArrayList<Note> notes, boolean append, Context c) throws FileNotFoundException, IOException
	{
		 FileOutputStream fos;       
		 if (append)
			 fos = c.openFileOutput(filename, Context.MODE_APPEND);
		 else
			 fos = c.openFileOutput(filename,Context.MODE_PRIVATE);
		 XmlSerializer xmlSerializer = Xml.newSerializer();
		 xmlSerializer.setOutput(fos, "UTF-8");
		 xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		 
		 for (Note note : notes) {
			 xmlSerializer.startTag("", "Note");
			 if (note.course != null) {
				 xmlSerializer.startTag("", "course");
				 xmlSerializer.text(note.course);
				 xmlSerializer.endTag("", "course");
			 }
			 if (note.topic != null) {
				 xmlSerializer.startTag("", "topic");
				 xmlSerializer.text(note.topic);
				 xmlSerializer.endTag("", "topic");
			 }
			 if (note.date != null) {
				 xmlSerializer.startTag("", "date");
				 xmlSerializer.text(note.date);
				 xmlSerializer.endTag("", "date");
			 }
			 if (note.recording != null) {
				 xmlSerializer.startTag("", "recording");
				 xmlSerializer.text(note.recording);
				 xmlSerializer.endTag("", "recording");
			 }
			 if (note.image != null) {
				 xmlSerializer.startTag("", "image");
				 xmlSerializer.text(note.image);
				 xmlSerializer.endTag("", "image");
			 }
			 if (note.timestamps != null) {
				 xmlSerializer.startTag("", "timestamps");
				 for (long x : note.timestamps) {
					 xmlSerializer.startTag("", "timestamp");
					 xmlSerializer.text(Long.toString(x));
					 xmlSerializer.endTag("",  "timestamp");
				 }
				 xmlSerializer.endTag("", "timestamps");
			 }
			 if (note.bookmarks != null) {
				 xmlSerializer.startTag("", "bookmarks");
				 for (Point p : note.bookmarks) {
					 xmlSerializer.startTag("", "bookmark");
					 xmlSerializer.startTag("", "x");
					 xmlSerializer.text(Float.toString(p.x));
					 xmlSerializer.endTag("",  "x");
					 xmlSerializer.startTag("", "y");
					 xmlSerializer.text(Float.toString(p.y));
					 xmlSerializer.endTag("",  "y");
					 xmlSerializer.endTag("",  "bookmark");
				 }
				 xmlSerializer.endTag("", "bookmarks");
			 }
			 xmlSerializer.endTag("", "Note");
		 }
		 xmlSerializer.endDocument();
		 xmlSerializer.flush();
		 fos.close();
	}
	
}
