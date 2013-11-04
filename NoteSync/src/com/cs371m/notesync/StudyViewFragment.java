package com.cs371m.notesync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Fragment for Study View
 * @author ctang
 *
 */
public class StudyViewFragment extends Fragment { //vs static inner class? 
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_IMAGE_PATH = "image_path";
    public static final String ARG_RECORDING_PATH = "recording_path";

    public StudyViewFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_study_view, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.imgDisplay);
        //? arguments Bundle vs savedInstanceState?
        Bundle args = this.getArguments();
        System.out.println("THIS IS A TEST!");
        if (args != null && args.containsKey(ARG_IMAGE_PATH)) {
        	String path = args.getString(ARG_IMAGE_PATH);
        	//TODO: ensure the notebook view tells us the audio file and optionally images
        	//TODO: validate correct path?
        	try {
        		Context c = rootView.getContext();
        		//save a file here for debug purposes
        		//File f = new File();
        		
        		//String appDirectory = c.getFilesDir().getAbsolutePath(); //getPath? //getFilesDir gives hidden internal directory /data/data
        		//String imgDirectory = c.getDir("img", Context.MODE_PRIVATE).getAbsolutePath(); //app_img
        		String externalDirectory = c.getExternalFilesDir(null).getAbsolutePath(); //storage/sdcard0/Android/data/com.cs371m.notesync/files
        		//c.getExternalFilesDir(type)
        		Bitmap bmap = BitmapFactory.decodeFile(externalDirectory+path);
            	if (bmap != null)
            		image.setImageBitmap(bmap); //works for external storage
            	else 
            		image.setImageResource(R.drawable.ic_launcher);  //TODO: use different image indicating bad file
        	} catch (Exception e) {
        		System.out.println("Failed to decode image file at path: " + path + " with error: " + e.getMessage());
        		image.setImageResource(R.drawable.ic_launcher);  //TODO: use different image indicating bad file
        	}
        }
        else
        	image.setImageResource(R.drawable.ic_launcher); //some default image TODO: use NoteSync logo
        return rootView;
    }
}