package com.cs371m.notesync;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NotesViewFragment extends ListFragment{
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static final int STUDY_VIEW_POS = 2;
	
	NotesAdapter adapter;
	MainActivity activity;

	public NotesViewFragment() {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.notes_view_fragment, container, false);
		// Construct the data source
		// Create the adapter to convert the array to views
		adapter = new NotesAdapter(rootView.getContext());
		// Add the collection of User objects to the adapter
		updateList();
		// Attach the adapter to a ListView
		this.setListAdapter(adapter);
		// add a listener to the listView
		//this.getListView(). //fails, not yet created
		
		
		return rootView;
	}
	

    //from http://stackoverflow.com/questions/16834678/android-setonitemclicklistener-event-of-listview-not-working-in-fragment
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView l = (ListView) getActivity().findViewById(android.R.id.list); //(ListView) rootView, listener never gets called with android.R.id.list...
		l.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position,
	                long id) { //clicking a note opens the study view with that particular note
	            
	        	//refresh activity
	        	activity = (MainActivity) getActivity();
	        	
	        	//set newly selected Note
	        	activity.mCurrentNote = adapter.getItem(position);
	        	
	        	Toast.makeText(getActivity().getApplicationContext(), "New Note Selected",
						Toast.LENGTH_SHORT).show(); //debug
	        	
	        	/*//Failed attempt to navigate to StudyView
	        	//go to the study view using a fragment transaction
	        	FragmentManager fragmentManager = getFragmentManager();
	        	Fragment studyView = fragmentManager.findFragmentById(fragmentManager.);
	        	FragmentTransaction transaction = getFragmentManager().beginTransaction();
	        	// Replace whatever is in the fragment_container view with this fragment,
	        	
	        	// and add the transaction to the back stack
	        	transaction.replace(R.id.notes_view_fragment, studyView); //R.id.fragment_container?
	        	transaction.addToBackStack(null);
	        	view.
	        	mViewPager.setCurrentItem(STUDY_VIEW_POS); //assume study view at position 2
	        	*/
	            
	            
	        }
		});
        
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (adapter == null) {
			adapter = new NotesAdapter(this.getActivity());
		}
		updateList();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.activity = (MainActivity) activity;
		} catch(ClassCastException e) {
			
		}
	}
	
	public void updateList() {
		adapter.clear();
		adapter.addAll(activity.notes);
	}
}
