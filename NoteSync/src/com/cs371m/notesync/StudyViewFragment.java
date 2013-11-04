package com.cs371m.notesync;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;

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
    public static final String STUDY_VIEW_LOG_TAG = "StudyViewFragment";
    
    public MediaPlayer mediaPlayer; //to prevent replaying TODO: move to service
    
    public StudyViewFragment() {
    	
    }

    //called everytime the view is displayed?
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_study_view, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.imgDisplay);
        //? arguments Bundle vs savedInstanceState?
        Bundle args = this.getArguments();
		Context c = rootView.getContext();
		
		//TODO: add pinch-zoom
		drawImage(args, image, c);
        
		//start playing recording //TODO: test playback unaffected when returning to view
		playRecording(args, c);

		//TODO: add controls
		MediaController mc = (MediaController) rootView.findViewById(R.id.mediaController);
		
		return rootView;
    }
    
    
    private void drawImage(Bundle args, ImageView image, Context c) {
    	if (args != null && args.containsKey(ARG_IMAGE_PATH)) {
        	String path = args.getString(ARG_IMAGE_PATH);
        	//TODO: ensure the notebook view tells us the audio file and optionally images
        	//TODO: validate correct path?
        	try {
        		//String appDirectory = c.getFilesDir().getAbsolutePath(); //getPath? //getFilesDir gives hidden internal directory /data/data
        		//String imgDirectory = c.getDir("img", Context.MODE_PRIVATE).getAbsolutePath(); //app_img
        		String externalDirectory = c.getExternalFilesDir(null).getAbsolutePath(); //storage/sdcard0/Android/data/com.cs371m.notesync/files
        		Bitmap bmap = BitmapFactory.decodeFile(externalDirectory+path);
            	if (bmap != null)
            		image.setImageBitmap(bmap); //works for external storage
            	else 
            		image.setImageResource(R.drawable.ic_launcher);  //TODO: use different image indicating bad file
        	} catch (Exception e) {
        		Log.e(STUDY_VIEW_LOG_TAG, "Failed to decode image file at path: " + path + " with error: " + e.getMessage());
        		image.setImageResource(R.drawable.ic_launcher);  //TODO: use different image indicating bad file
        	}
        }
        else
        	image.setImageResource(R.drawable.ic_launcher); //some default image TODO: use NoteSync logo
    	
    }
    
    private void playRecording(Bundle args, Context c) {
    	if (args != null && args.containsKey(ARG_RECORDING_PATH)) {
			String path = args.getString(ARG_RECORDING_PATH);
			String externalDirectory = c.getExternalFilesDir(null).getAbsolutePath(); //storage/sdcard0/Android/data/com.cs371m.notesync/files
			File f = new File(externalDirectory+path);
    		if (!f.exists()) 
    			Log.e(STUDY_VIEW_LOG_TAG, "Failed to find recording file at path: " + externalDirectory+path);
    		else {
				Uri myUri = Uri.fromFile(f); // initialize Uri here
	    		
				//TODO: prevent replaying over itself
				
				//if (mediaPlayer == null) {
					mediaPlayer = new MediaPlayer();
			        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			        try {
						mediaPlayer.setDataSource(c, myUri);
						mediaPlayer.prepare(); //FIXME: async won't work
					} catch (IOException e) { //TODO: handle other exceptions
						Log.e(STUDY_VIEW_LOG_TAG, e.getMessage());
					} catch (IllegalArgumentException e) {
						Log.e(STUDY_VIEW_LOG_TAG, e.getMessage());
					}
			        mediaPlayer.start();
				//}
    		}
	      
		}
    }
    /*
    public void onPrepared(MediaPlayer player) {
        player.start();
    }*/
}