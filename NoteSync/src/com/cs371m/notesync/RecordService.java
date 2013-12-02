package com.cs371m.notesync;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class RecordService extends Service{

	private final String TAG = "RecordService";
	private MediaRecorder mRecorder;
	private WakeLock mLock;
	Time time = new Time();
	public String mFileName;
	private String startRecTime;
	private boolean isRunning = false;

	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		RecordService getService() {
			return RecordService.this;
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

		mRecorder = new MediaRecorder();
		mLock = ((PowerManager)this.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "recordlock");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		if (mRecorder != null) {
			try {
				mRecorder.stop();
				mRecorder.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
			
		}
		if (mLock != null) {
			try {
				mLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
		}
		mRecorder = null;
		isRunning = false;

	}

	public void Stop() {
		if (isRunning) {
			Toast.makeText(this, "Stopped Recording", Toast.LENGTH_LONG).show();
			Log.d(TAG, "onDestroy");
			mRecorder.stop();
			mRecorder.release();
			mLock.release();
			mRecorder = null;
			isRunning = false;
		}
	}

	public void Record() {
		if (!isRunning) {
			isRunning = true;

			Toast.makeText(this, "Started Recording", Toast.LENGTH_LONG).show();
			Log.d(TAG, "onStart");
			mLock.acquire();
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			//Retrieve and set time stamp 
			time.setToNow();
			startRecTime = Long.toString(time.toMillis(false)); //ignore DST?

			mFileName = this.getApplicationContext().getFilesDir().getAbsolutePath();

			mFileName += "/"+startRecTime+".3gp";

			Log.v(TAG, "File name created:"+ mFileName+ "\n");

			mRecorder.setOutputFile(mFileName);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			try {
				mRecorder.prepare();
			} catch (IOException e) {
				Log.e(TAG, "prepare() failed: " + e.getMessage());
			}

			mRecorder.start();
		} else {
			Log.v(TAG, "ALREADY RUNNING");
		}
	}
}
