package com.cs371m.notesync;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Generic fragment
 * @author ctang
 *
 */
public class SectionFragment extends Fragment { //vs static inner class? 
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public SectionFragment() {
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	//TODO: replace with polymorphism of section types?
    	switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
    		case 1: //TODO: constants
    			return createDummyView(inflater, container, savedInstanceState);
    		case 2:
    			return createDummyView(inflater, container, savedInstanceState);
    		case 3: 
    			return createStudyView(inflater, container, savedInstanceState);
    		default:
    			throw new IllegalArgumentException("Invalid Section number + " 
    					+ getArguments().getInt(ARG_SECTION_NUMBER) + "+ was given!");
    	}
    }
    
    //helper to create a dummy view
    private View createDummyView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
        dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
    
    //TODO: add Recorder view
    
    //TODO: add Notebook view
    
    //TODO: add Settings view
    
    /**
	 * Helper to create a StudyView, which allows viewing of images and audio playback of notes
	 */
    private View createStudyView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	View rootView = inflater.inflate(R.layout.fragment_study_view, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.imgDisplay);
        if (savedInstanceState.containsKey("IMAGE_PATH")) 
        	//TODO: ensure the notebook view tells us the audio file and optionally images
        	//TODO: define this string in constants
        	image.setImageBitmap(BitmapFactory.decodeFile(savedInstanceState.getString("IMAGE_PATH"))); //TODO: test on phone
        else
        	image.setImageResource(R.drawable.ic_launcher); //some default image TODO: use NoteSync logo
        
        
        
        return rootView;
    }
}