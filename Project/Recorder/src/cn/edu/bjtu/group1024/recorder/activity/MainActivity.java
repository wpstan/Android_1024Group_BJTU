package cn.edu.bjtu.group1024.recorder.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import cn.edu.bjtu.group1024.recorder.R;
import cn.edu.bjtu.group1024.recorder.baidu.IRecorderConstant;
import cn.edu.bjtu.group1024.recorder.service.AudioRecorder;
import cn.edu.bjtu.group1024.recorder.service.AudioRecorder.MessageProto;
import cn.edu.bjtu.group1024.recorder.utils.PreferenceUtils;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.oauth.BaiduOAuth.OAuthListener;

public class MainActivity extends Activity implements IRecorderConstant {

	private boolean mIsLogin = false;
	private String mLoginName;

	private boolean mIsRecording = false;
	private Button mButton;

	private UpdateDuration mUpd = null;
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
				if (mUpd != null)
					mUpd.cancel(true);
				break;
			case AudioRecorder.MSG_TIME_START:
				MessageProto val = (MessageProto) msg.obj;
				if (mUpd != null)
					mUpd.cancel(true);
				mUpd = new UpdateDuration();
				mUpd.execute(val.value, val.value, val.value);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	});

	// 更新主题
	public void updateTheme() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sharedPref.getBoolean("use_dark_theme", false))
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
	}

	// 录音按钮点击事件
	public void TouchRecord(View view) {
		mIsRecording = !mIsRecording;
		if (mIsRecording) {
			TouchStartRecord(view);
		} else {
			TouchStopRecord(view);
		}
	}

	private void TouchStartRecord(View view) {
		sendMsgServ(AudioRecorder.MSG_START_RECORD);
	}

	private void TouchStopRecord(View view) {
		sendMsgServ(AudioRecorder.MSG_STOP_RECORD);
	}

	// 更新按钮的背景图片
	private void updataImageBackground(boolean isRecording) {
		if (isRecording) {
			mButton.setBackgroundResource(R.drawable.pause);
		} else {
			mButton.setBackgroundResource(R.drawable.play);
		}
		this.mIsRecording = isRecording;
	}

	// 更新播放时长TextView显示
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
		if (mIsLogin) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("退出")
					.setMessage("确定退出吗？")
					.setPositiveButton("退出",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									PreferenceUtils.clearData();// 退出后，清空本地存储的用户名和token
									mIsLogin = false;
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
					PreferenceUtils.setLoginName(response.getUserName());
					PreferenceUtils.setLoginStatus(true);
					PreferenceUtils.setToken(response.getAccessToken());
					mIsLogin = true;

				}

				@Override
				public void onCancel() {

				}
			});
		}
	}

	private void OpenFileList() {
		Intent intent = new Intent(MainActivity.this, FileListActivity.class);
		intent.putExtra("islogin", mIsLogin);
		intent.putExtra("username", mLoginName);
		startActivity(intent);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateTheme();
		setContentView(R.layout.activity_main);
		// 初始化PreferenceUtils
		PreferenceUtils.init(this);
		mButton = (Button) findViewById(R.id.button);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		startService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mUpd != null)
			mUpd.cancel(true);
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

		if (mUpd != null)
			mUpd.cancel(true);
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
		mIsLogin = PreferenceUtils.getLoginStatus();
		if (mIsLogin) {
			mLoginName = PreferenceUtils.getLoginName();
			menu.getItem(2).setTitle(mLoginName);
		}
		return super.onCreateOptionsMenu(menu);
	}

}
