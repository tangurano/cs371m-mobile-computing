package com.cs371m.notesync;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NotesViewFragment extends ListFragment{
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	
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
		return rootView;
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
