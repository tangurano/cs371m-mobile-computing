package com.cs371m.notesync;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class PlaybackService extends Service{

	private final String TAG = "PlaybackService";
	private MediaPlayer mPlayer;
	private WakeLock mLock;
	Time time = new Time();
	public String mFileName;
	private boolean isRunning = false;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		PlaybackService getService() {
			return PlaybackService.this;
		}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//TODO do something useful
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	@Override
	public void onCreate() {
		Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");

		mPlayer = new MediaPlayer();
		mLock = ((PowerManager)this.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "playlock");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
		}
		if (mLock != null) {
			mLock.release();
		}
		mPlayer = null;
		isRunning = false;

	}

	public void Stop() {
		if (isRunning) {
			Toast.makeText(this, "Stopped Playing", Toast.LENGTH_LONG).show();
			Log.d(TAG, "onDestroy");
			mPlayer.stop();
			mPlayer.release();
			mLock.release();
			mPlayer = null;
			isRunning = false;
		}
	}

	public void Play(String path) {
		//if (activity.mCurrentNote.recording != null && mediaPlayer == null)
		//	playRecording(activity.mCurrentNote.recording, rootView.getContext());
		//TODO: even if running, overwrite by playing again from start
		if (!isRunning) {
			isRunning = true;

			Toast.makeText(this, "Started Playing", Toast.LENGTH_LONG).show();
			Log.d(TAG, "onStart");
			mLock.acquire();
			mPlayer = new MediaPlayer();

			try {
				mPlayer.prepare();
			} catch (IOException e) {
				Log.e(TAG, "prepare() failed: " + e.getMessage());
			}

			mPlayer.start();
		} else {
			Log.v(TAG, "ALREADY RUNNING");
		}
	}
	
	/*
	 * private void playRecording(String path, Context c) {
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
	 */
}
