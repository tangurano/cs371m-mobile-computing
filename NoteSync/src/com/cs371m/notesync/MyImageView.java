package com.cs371m.notesync;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	ArrayList<Point> points = new ArrayList<Point>();
	Paint paint = new Paint();
	MainActivity activity;

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (MainActivity) context;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (activity != null && activity.mCurrentNote != null && activity.mCurrentNote.bookmarks != null) {
			for (Point point : activity.mCurrentNote.bookmarks) {
				canvas.drawCircle(point.x, point.y, 20, paint);
				// Log.d(TAG, "Painting: "+point);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    return gestureDetector.onTouchEvent(event);
	}

	final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
		public void onLongPress(MotionEvent event) {
			Log.e("", "Longpress detected");
			if (activity != null && activity.mCurrentNote != null) {
				if (activity.mCurrentNote.bookmarks == null) 
					activity.mCurrentNote.bookmarks = new ArrayList<Point>();
				if (activity.mCurrentNote.bookmarks.size() < activity.mCurrentNote.timestamps.size()) {
					Point point = new Point(event.getX(), event.getY());
					activity.mCurrentNote.bookmarks.add(point);
					invalidate();
					Log.d("", "point: " + point);
				}

			}
		}
	});
}

