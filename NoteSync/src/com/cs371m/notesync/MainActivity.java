package com.cs371m.notesync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private static final String LOG_TAG = "AudioRecordTest";
	private static String mFileName = null;

	private Button mRecordButton = null;
	private MediaRecorder mRecorder = null;

	private Button   mPlayButton = null;
	private MediaPlayer   mPlayer = null;
	private String startRecTime=null;
	boolean mStartRecording, mStartPlaying;

	private WakeLock mWakeLock;

	protected static ArrayList<Note> notes;
	//0: Title 1: Class Name 2: Tag(s)
	//Create enumeration class of the 3 types above
	protected static EditText [] txtInputVals= new EditText[3];
	protected static String [] inputVals= new String[3];
	Time time = new Time();
	private static boolean clickedOk=false; 
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	public void showEditRecInfoDialog() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new EditRecInfoDialogFragment();
		dialog.show(getFragmentManager(), "Edit Title Fragment");
	}

	private RecordService mBoundService;
	private boolean mIsBound = false;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mBoundService = ((RecordService.LocalBinder)service).getService();

			// Tell the user about this for our demo.
			Toast.makeText(mBoundService, "RecordService connected",
					Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			Toast.makeText(mBoundService, "RecordService disconnected",
					Toast.LENGTH_SHORT).show();
			mBoundService = null;

		}
	};

	void doBindService() {
		// Establish a connection with the service.  We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		boolean ret = bindService(new Intent(this, 
				RecordService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}


	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface
	public void onDialogPositiveClick() {
		// User touched the dialog's positive button
		Log.v(LOG_TAG, "clicked on pos");
	}

	public void onDialogNegativeClick(DialogFragment dialog) {
		// User touched the dialog's negative button
		Log.v(LOG_TAG, "clicked on neg");
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mWakeLock.acquire();
		mPlayer = new MediaPlayer();
		try {
			if (mFileName != null) {
				mPlayer.setDataSource(mFileName);
				mPlayer.prepare();
				mPlayer.start();
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mWakeLock.release();
		mPlayer = null;

		//Prompt for edit dialog

	}

	private void startRecording() {
		/*
		mWakeLock.acquire();
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		//Retrieve and set time stamp 
		time.setToNow();
		startRecTime=Long.toString(time.toMillis(false)); //ignore DST?
		//Setting the audio file save path
		//mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

		mFileName = this.getApplicationContext().getFilesDir().getAbsolutePath();
		//mFileName = this.getApplicationContext().getFilesDir().getAbsolutePath();
		//File f = this.getApplicationContext().openFileOutput(name, mode).getFilesDir();
		//mFileName += "/NoteSync/rec/"+startRecTime+".3gp";
		mFileName += "/"+startRecTime+".3gp";

		Log.v(LOG_TAG, "File name created:"+ mFileName+ "\n");

		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed: " + e.getMessage());
		}

		mRecorder.start();
		 */
		if (mIsBound)
			mBoundService.Record();
	}

	private void stopRecording() {
		/*
		mRecorder.stop();
		mRecorder.release();
		mWakeLock.release();
		mRecorder = null;
		 */
		if (mIsBound) {
			mFileName = ((RecordService) mBoundService).mFileName;
			mBoundService.Stop();
		}	
		showEditRecInfoDialog();
	}


	public static class EditRecInfoDialogFragment extends DialogFragment 
	{
		public interface EditRecInfoDialogListener 
		{
			public void onDialogPositiveClick(DialogFragment dialog);
			public void onDialogNegativeClick(DialogFragment dialog);
		}

		//EditRecInfoDialogListener mListener;
		/*
		// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	    @Override
	    public void onAttach(Activity activity) 
	    {
	        super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	        try 
	        {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	            mListener = (EditRecInfoDialogListener) activity;
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement NoticeDialogListener");
	        }
	    }
		 */
		public Dialog onCreateDialog(Bundle savedInstanceState) 
		{

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity ());
			// Get the layout inflater
			LayoutInflater inflater = getActivity ().getLayoutInflater();
			View diag_view = inflater.inflate(R.layout.edit_title_dialog, null);
			//DialogInterface onClickInterface=new DialogInterface.OnClickListener();
			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			builder.setView(inflater.inflate(R.layout.edit_title_dialog, null))
			// Add action buttons
			.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) 
				{

					//Retrieve entered Title, Course name and Tags.
					txtInputVals[0]= (EditText) ((AlertDialog) dialog).findViewById(R.id.changeTitle); 
					inputVals[0]= txtInputVals[0].getText().toString();
					txtInputVals[1]= (EditText) ((AlertDialog) dialog).findViewById(R.id.changeClass); 
					inputVals[1]= txtInputVals[1].getText().toString();
					txtInputVals[2]= (EditText) ((AlertDialog) dialog).findViewById(R.id.changeTags); 
					inputVals[2]= txtInputVals[2].getText().toString();
					clickedOk=true;

				}

			}
					)
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							//Give default name
							EditRecInfoDialogFragment.this.getDialog().cancel();
						}
					}); 
			//Return the created dialogue box
			return builder.create();
		}

		@Override
		public void onDetach()
		{
			super.onDetach();
			Log.v(LOG_TAG, "onDetach() dialog\n");
			if (clickedOk)
			{
				//Fill in a Note obj w/ the input str values
				//Add this entry to notebook view
				Note perNote= new Note();
				perNote.topic=inputVals[0];
				perNote.course=inputVals[1];
				notes.add(perNote);
			}
			clickedOk=false;
		}

	}

	public void onClickStartRec(View v) {
		onRecord(mStartRecording);

		if (mStartRecording) {
			mRecordButton=(Button) findViewById(R.id.mRecordButton);
			mRecordButton.setText(R.string.stopRecord);
		} else {
			mRecordButton.setText(R.string.startRecord);
		}
		mStartRecording = !mStartRecording;
	}

	public void onClickStartPlay(View v) 
	{
		onPlay(mStartPlaying);
		// notes = new ArrayList<Note>();
		if (mStartPlaying) {
			mPlayButton=(Button) findViewById(R.id.mPlayButton);
			mPlayButton.setText(R.string.stopPlaying);
		} else {
			mPlayButton.setText(R.string.startPlaying);
		}
		mStartPlaying = !mStartPlaying;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mStartRecording = true;
		mStartPlaying = true;
		//FIXME: doesn't work b/c fragment not visible
		//mRecordButton=(Button) findViewById(R.id.mRecordButton);
		//mPlayButton=(Button) findViewById(R.id.mPlayButton);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mWakeLock = ((PowerManager)this.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "recordlock");

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(
					actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		if (tab.getPosition() == 1) {
			FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
			if (adapter != null) {
				NotesViewFragment frag = (NotesViewFragment) adapter.instantiateItem(mViewPager, 1);
				if (frag != null)
					frag.updateList();
			}
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch(position) {
			case 0:
				return new RecordViewFragment();
			case 1:
				return new NotesViewFragment();
			default:
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

			return rootView;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		doBindService();
		if (mWakeLock == null) {
			mWakeLock = ((PowerManager)this.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "recordlock");
		}
		try {
			notes = Helper.loadNotes(this.getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			notes = new ArrayList<Note>();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//doUnbindService();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
		try {
			Helper.writeNotes(notes, false, this.getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		//doUnbindService();
		try {
			Helper.writeNotes(notes, false, this.getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//doUnbindService();
		try {
			Helper.writeNotes(notes, false, this.getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
