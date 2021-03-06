package com.cs371m.notesync;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
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
	ArrayList<Note> reversedNotes = new ArrayList<Note>();

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
	/*
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	  if (v.getId()==getView().getId() && activity != null 
			  && activity.notes != null) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle(activity.notes.get(info.position).topic);
	    String[] menuItems = getResources().getStringArray(R.array.menu);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	  }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  String[] menuItems = getResources().getStringArray(R.array.menu);
	  String menuItemName = menuItems[menuItemIndex];
	  String listItemName = activity.notes.get(info.position).topic;

	  //case menuItemIndex
	  return true;
	}
	*/
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		 Note note = (Note) getListView().getItemAtPosition(position);
		 MainActivity main = ((MainActivity) getActivity());
		 main.mCurrentNote = note;
		 main.mViewPager.setCurrentItem(2);
		 
		 Toast.makeText(getActivity().getApplicationContext(), "New Note Selected",
					Toast.LENGTH_SHORT).show(); //debug
//     	
//     	//TODO: re-create the StudyView, thus displaying a new image 
//     	
//     	
//     	//TODO: tell the background PlayBackService to play here, instead of on view creation
//     	activity.mBoundPlayService.Play(activity.mCurrentNote.recording);
		 
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
		reversedNotes = new ArrayList<Note>(activity.notes);
		Collections.reverse(reversedNotes);
		adapter.addAll(reversedNotes);
	}
}
