package com.habzy.video;

import java.io.FileDescriptor;
import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class RealVideoPlayBufferActivity extends Activity implements Callback {
	private static final String TAG = "RealVideoPlayBufferActivity";
	private static final int MENU_PLAY = 1;

	private MediaPlayer mPlayer;
	private SurfaceView mShownSurfaceView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mShownSurfaceView = (SurfaceView) findViewById(R.id.shown);

		SurfaceHolder holder = mShownSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void playMedia() {
		// mCameraLayout.setVisibility(View.GONE);

		if (null == mPlayer) {
			try {

				// the video from SDCard
				// File sdFile = Environment.getExternalStorageDirectory(); 
				// this.mediaPlayer = new MediaPlayer();  
				// this.mediaPlayer.setDataSource(sdFile.getAbsoluteFile() + File.separator + "welcome.3gp");

				// the video from res/ 
				// this.mediaPlayer = MediaPlayer.create(this, R.raw.welcome);

				// the video from web-site
				// this.mediaPlayer.setDataSource("http://xy2.163.com/download/down/wukong.mp3");

				// The video from asset.
				AssetFileDescriptor aFD = getAssets().openFd("play.3gp");

				mPlayer = new MediaPlayer();
				mPlayer.setDisplay(mShownSurfaceView.getHolder());
				// Play with file which record.
				// mPlayer.setAudioStreamType(AudioManager.)
				// mPlayer.setDataSource(mRecFile.getAbsolutePath());
				// mPlayer.setDataSource(FileDescriptor)

				mPlayer.setDataSource(aFD.getFileDescriptor());

				mPlayer.prepareAsync(); 
//				mPlayer.prepare();
				mPlayer.start();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onMenuOpened(int, android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "Create menu....");
		menu.add(Menu.NONE, MENU_PLAY, Menu.NONE, R.string.menu_play);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Click menu....id:" + item.getItemId());
		switch (item.getItemId()) {
		case MENU_PLAY: {
			playMedia();
		}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
}