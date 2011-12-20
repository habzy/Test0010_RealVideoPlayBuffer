package com.habzy.video;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
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
	private FileDescriptor mfd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mShownSurfaceView = (SurfaceView) findViewById(R.id.shown);

		SurfaceHolder holder = mShownSurfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Use socket
		// initSocket();

		// use fileoutputstream
		initFOS();

	}

	private void playMedia() {
		// mCameraLayout.setVisibility(View.GONE);

		if (null == mPlayer) {
			try {

				mPlayer = new MediaPlayer();
				mPlayer.setDisplay(mShownSurfaceView.getHolder());

				// the video from SDCard
				// File sdFile = Environment.getExternalStorageDirectory(); 
				// this.mediaPlayer = new MediaPlayer();  
				// this.mediaPlayer.setDataSource(sdFile.getAbsoluteFile() + File.separator + "welcome.3gp");

				// the video from res/ 
				// this.mediaPlayer = MediaPlayer.create(this, R.raw.welcome);

				// the video from web-site
				// this.mediaPlayer.setDataSource("http://xy2.163.com/download/down/wukong.mp3");

				// Play with file which record.
				// mPlayer.setAudioStreamType(AudioManager.)
				// mPlayer.setDataSource(mRecFile.getAbsolutePath());

				// The video from asset.
				// AssetFileDescriptor aFD = getAssets().openFd("play.3gp");
				// mPlayer.setDataSource(aFD.getFileDescriptor(),
				// aFD.getStartOffset(), aFD.getLength());
				// mfd = aFD.getFileDescriptor();

				// The video from local socket.
				// transBuffer();
				// mfd = receiver.getFileDescriptor();

				// The video from OutputStream.
				mfd = fos.getFD();

				mPlayer.setDataSource(mfd, 49, Integer.MAX_VALUE
						- (Integer.MAX_VALUE % 32));

				// mPlayer.setDataSource(is.)

				mPlayer.prepare();
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

	FileOutputStream fos;

	private void initFOS() {
//		fos = new FileOutputStream(new FileDescriptor());
//		new Thread() {
//			public void run() {
//				byte[] buffer = new byte[1024];
//				while (getBuffer(buffer)) {
//					try {
//						fos.write(buffer, 0, 1024);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//			};
//		}.start();
		
		try {
			fos = new FileOutputStream(getAssets().openFd("play.3gp").getFileDescriptor());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private LocalSocket receiver, sender;
	private LocalServerSocket lss;

	/**
	 * 
	 */
	private void initSocket() {
		receiver = new LocalSocket();
		try {
			lss = new LocalServerSocket("Video");
			receiver.connect(new LocalSocketAddress("Video"));
			receiver.setReceiveBufferSize(500000);
			receiver.setSendBufferSize(500000);
			sender = lss.accept();
			sender.setReceiveBufferSize(500000);
			sender.setSendBufferSize(500000);
		} catch (IOException e) {
			finish();
			return;
		}
	}

	private void transBuffer() {
		new Thread() {
			public void run() {
				byte[] buffer = new byte[1024];
				while (getBuffer(buffer)) {
					try {
						OutputStream os = sender.getOutputStream();
						os.write(buffer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
		}.start();
	}

	FileInputStream fis;
	int offset = 0;
	AssetFileDescriptor aFD;

	private boolean getBuffer(byte[] buffer) {
		if (null == fis || null == aFD) {
			try {
				aFD = getAssets().openFd("play.3gp");
				fis = new FileInputStream(aFD.getFileDescriptor());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Simulate recorder of rate 50.
		try {
			Thread.sleep(20);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Log.d(TAG, "offset:" + offset);
		if (offset > aFD.getLength() - 1024) {
			return false;
		}

		try {
			fis.read(buffer, 0, 1024);
			offset += 1024;
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
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
	protected void onDestroy() {
		if (null != lss) {
			try {
				lss.close();
				receiver.close();
				sender.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (null != mPlayer) {
			mPlayer.release();
			mPlayer = null;
		}

		super.onDestroy();
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