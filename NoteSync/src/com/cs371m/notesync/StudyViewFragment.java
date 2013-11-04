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
    
    MainActivity activity; //always null?
    
    public StudyViewFragment() {
    	
    }

    //called everytime the view is displayed?
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_study_view, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.imgDisplay);
        //? arguments Bundle vs savedInstanceState? vs using mainActivity state?
        	//external useful for testing
		//String externalDirectory = rootView.getContext().getExternalFilesDir(null).getAbsolutePath(); //storage/sdcard0/Android/data/com.cs371m.notesync/files
		//String appDirectory = c.getFilesDir().getAbsolutePath(); //getPath? //getFilesDir gives hidden internal directory /data/data
		//String imgDirectory = c.getDir("img", Context.MODE_PRIVATE).getAbsolutePath(); //app_img
		
		activity = (MainActivity) getActivity(); //re-init here?
		if (activity.mCurrentNote != null ) {
			if (activity.mCurrentNote.image != null)
				drawImage(activity.mCurrentNote.image, image); //TODO: add pinch-zoom ///data/data/com.cs371m.notesync/files/IMG_20131104_102808.jpg
			else
				image.setImageResource(R.drawable.ic_launcher); //TODO: use NoteSync logo
			
			//TODO: test playback unaffected when returning to view
			if (activity.mCurrentNote.recording != null && mediaPlayer == null)
				playRecording(activity.mCurrentNote.recording, rootView.getContext());
		} else
			image.setImageResource(R.drawable.ic_launcher); //TODO: use NoteSync logo
		
		//TODO: add controls
		MediaController mc = (MediaController) rootView.findViewById(R.id.mediaController);
		
		return rootView;
    }
    
    
    
    private void drawImage(String path, ImageView image) {
    	//TODO: validate correct path?
    	try {
    		if (!(new File(path).exists())) 
    			throw new Exception("no such file exists");
    		Bitmap bmap = BitmapFactory.decodeFile(path);
        	if (bmap == null)
        		throw new Exception("decoding file failed");
    		image.setImageBitmap(bmap); //works for external storage
        		
    	} catch (Exception e) {
    		Log.e(STUDY_VIEW_LOG_TAG, "Failed to display image at path: " + path + " with error: " + e.getMessage());
    		image.setImageResource(R.drawable.ic_launcher);  //TODO: use different image indicating bad file
    	}
    }
    
    private void playRecording(String path, Context c) {
		File f = new File(path);
		if (!f.exists()) 
			Log.e(STUDY_VIEW_LOG_TAG, "Failed to find recording file at path: " + path);
		else {
			Uri myUri = Uri.fromFile(f); // initialize Uri here
    		
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
			
		}
    }
    /*//causes failure to play
    public void onPrepared(MediaPlayer player) {
        player.start();
    }*/
}