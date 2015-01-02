package cn.edu.bjtu.group1024.recorder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.edu.bjtu.group1024.recorder.AudioRecorder.MessageProto;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.oauth.BaiduOAuth.OAuthListener;
import com.baidu.pcs.BaiduPCSClient;

public class MainActivity extends Activity {
	// 百度的api_key和secret_key
	private final static String API_KEY = "L6g70tBRRIXLsY0Z3HwKqlRE";
	private final static String SECRET_KEY = "S2eyPbtdvVFkj8wDj6pZeidqzSNNkDuc";
	private final static String ROOT_PATH = "/apps/pcstest_oauth";
	private final static String SHARED_PREFF_LOGIN = "islogin";
	private final static String SHARED_PREFF_NAME = "loginname";
	BaiduPCSClient mBaiduClient = new BaiduPCSClient();
	private boolean isLogin = false;
	private String mLoginName;
	private SharedPreferences mSharedPreferences;

	private boolean isRecording = false;
	private Button mButton;
	private Messenger msgService = null;
	private final Messenger mMessenger = new Messenger(new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AudioRecorder.MSG_START_RECORD:
				sendMsgServ(AudioRecorder.MSG_TIME_START);
				updataImageBackground(true);
				break;
			case AudioRecorder.MSG_STOP_RECORD:
				updataImageBackground(false);
				if (upd != null)
					upd.cancel(true);
				break;
			case AudioRecorder.MSG_TIME_START:
				MessageProto val = (MessageProto) msg.obj;
				if (upd != null)
					upd.cancel(true);
				upd = new UpdateDuration();
				upd.execute(val.value, val.value, val.value);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	});

	private UpdateDuration upd = null;

	public void updateTheme() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sharedPref.getBoolean("use_dark_theme", false))
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
	}

	public void TouchRecord(View view) {
		isRecording = !isRecording;
		if (isRecording) {
			TouchStartRecord(view);
		} else {
			TouchStopRecord(view);
		}
	}

	private void updataImageBackground(boolean isRecording) {
		if (isRecording) {
			mButton.setBackgroundResource(R.drawable.pause);
		} else {
			mButton.setBackgroundResource(R.drawable.play);
		}
		this.isRecording = isRecording;
	}

	private void TouchStartRecord(View view) {
		sendMsgServ(AudioRecorder.MSG_START_RECORD);
	}

	private void TouchStopRecord(View view) {
		sendMsgServ(AudioRecorder.MSG_STOP_RECORD);
	}

	private void updateDuration(long t) {
		TextView txt = (TextView) findViewById(R.id.text_duration);
		long t_now = System.currentTimeMillis();
		long elapse = (t_now - t) / 1000;
		txt.setText("时长 : " + Long.toString(elapse) + "秒");
	}

	private class UpdateDuration extends AsyncTask<Long, Long, Long> {

		@Override
		protected Long doInBackground(Long... arg0) {
			while (true) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				publishProgress(arg0);
				if (isCancelled())
					break;
			}
			return arg0[0];
		}

		protected void onProgressUpdate(Long... progress) {
			updateDuration(progress[0]);
		}

		protected void onPostExecute(Long result) {
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_settings:
			OpenSettings();
			return true;
		case R.id.action_filelist:
			OpenFileList();
			return true;
		case R.id.action_baidu:
			BaiduLogin(item);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// 百度链接登陆
	private void BaiduLogin(final MenuItem item) {
		if (isLogin) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("退出")
					.setMessage("确定退出吗？")
					.setPositiveButton("退出",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									setLoginName("");
									setLoginStatus(false);
									isLogin = false;
									item.setTitle("登陆");
								}

							}).setNegativeButton("取消", null).show();
		} else {

			BaiduOAuth oAuth = new BaiduOAuth();
			oAuth.startOAuth(this, API_KEY, new OAuthListener() {

				@Override
				public void onException(String e) {

				}

				@Override
				public void onComplete(BaiduOAuthResponse response) {
					item.setTitle(response.getUserName());
					setLoginName(response.getUserName());
					setLoginStatus(true);
					isLogin = true;
				}

				@Override
				public void onCancel() {

				}
			});
		}
	}

	private void OpenFileList() {
		startActivity(new Intent(MainActivity.this, FileListActivity.class));
	}

	private void OpenSettings() {
		startActivity(new Intent(MainActivity.this, SettingsActivity.class));
	}

	private void startService() {
		startService(new Intent(MainActivity.this, AudioRecorder.class));
		bindService(new Intent(this, AudioRecorder.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			msgService = new Messenger(service);
			try {
				Message msg = Message.obtain(null,
						AudioRecorder.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				msgService.send(msg);
				sendMsgServ(AudioRecorder.MSG_GET_STATUS);
			} catch (RemoteException e) {
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			msgService = null;
		}
	};

	private void sendMsgServ(int msg) {

		if (msgService == null) {
			return;
		}
		try {
			msgService.send(Message.obtain(null, msg, msg, 0));
		} catch (RemoteException e) {
		}
	}

	// 从本地获取登陆状态
	private boolean getLoginStatus() {
		return mSharedPreferences.getBoolean(SHARED_PREFF_LOGIN, false);
	}

	// 设置本地登陆状态
	private void setLoginStatus(boolean status) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(SHARED_PREFF_LOGIN, status);
		editor.commit();
	}

	// 从本地获取登陆用户名
	private String getLoginName() {
		return mSharedPreferences.getString(SHARED_PREFF_NAME, "");
	}

	// 设置本地登陆用户名
	private void setLoginName(String name) {
		Editor editor = mSharedPreferences.edit();
		editor.putString(SHARED_PREFF_NAME, name);
		editor.commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSharedPreferences = getSharedPreferences("group1024", MODE_PRIVATE);
		updateTheme();
		setContentView(R.layout.activity_main);

		mButton = (Button) findViewById(R.id.button);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		startService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (upd != null)
			upd.cancel(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		startService();
		sendMsgServ(AudioRecorder.MSG_GET_STATUS);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (sharedPref.getBoolean("stop_record_quit", false))
			sendMsgServ(AudioRecorder.MSG_STOP_RECORD);

		if (upd != null)
			upd.cancel(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startService();
		sendMsgServ(AudioRecorder.MSG_GET_STATUS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		isLogin = getLoginStatus();
		if (isLogin) {
			mLoginName = getLoginName();
			menu.getItem(2).setTitle(mLoginName);
		}
		return super.onCreateOptionsMenu(menu);
	}

	//
	// // API Key 7QZKIWYxikvOK4MiLiYMe2cG
	// // Secret Key S2eyPbtdvVFkj8wDj6pZeidqzSNNkDuc
	// private final static String API_KEY = "L6g70tBRRIXLsY0Z3HwKqlRE";
	// private final static String SECRET_KEY =
	// "S2eyPbtdvVFkj8wDj6pZeidqzSNNkDuc";
	// private final static String ROOT_PATH = "/apps/pcstest_oauth";
	// BaiduPCSClient api = new BaiduPCSClient();
	//
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_main);
	// BaiduOAuth oauth = new BaiduOAuth();
	// oauth.startOAuth(this, API_KEY, new OAuthListener() {
	//
	// @Override
	// public void onException(String arg0) {
	//
	// Toast.makeText(MainActivity.this, "onException: " + arg0,
	// Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// public void onComplete(BaiduOAuthResponse arg0) {
	//
	// api.setAccessToken(arg0.getAccessToken());
	// new Thread() {
	// public void run() {
	// final BaiduPCSActionInfo.PCSQuotaResponse info = api
	// .quota();
	// runOnUiThread(new Runnable() {
	// public void run() {
	// Toast.makeText(
	// MainActivity.this,
	// "onComplete: " + info.total + " used:"
	// + info.used, Toast.LENGTH_SHORT)
	// .show();
	// Log.d("tanshuai", " total:" + info.total / 1024
	// / 1024 + "MB" + ", used:" + info.used
	// / 1024 / 1024 + "MB");
	// }
	// });
	// };
	// }.start();
	// }
	//
	// @Override
	// public void onCancel() {
	//
	// Toast.makeText(MainActivity.this, "onCancel: ",
	// Toast.LENGTH_SHORT).show();
	//
	// }
	// });
	// }
}
