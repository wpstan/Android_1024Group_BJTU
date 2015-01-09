package cn.edu.bjtu.group1024.recorder.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;

public class AudioRecorder extends Service {
	private MediaRecorder mRecord = null;
	private String Filename = null;
	private String Folder = "RecorderGroup1024";
	private Messenger mClient = null;
	private boolean recording = false;
	private long StartRecord = 0;
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	private OnSharedPreferenceChangeListener listener = null;
	private SharedPreferences prefs = null;
	private Integer S_AudioFormat = 1;
	private Integer S_MaxFileSize = 0;
	private Integer S_MaxTimeRecord = 0;
	private String S_Folder;
	private String S_FileName;

	private static final Map<Integer, String> file_extension;
	static {
		file_extension = new HashMap<Integer, String>();
		file_extension.put(MediaRecorder.OutputFormat.THREE_GPP, "3gp");
		file_extension.put(MediaRecorder.OutputFormat.MPEG_4, "mp4");
		file_extension.put(MediaRecorder.OutputFormat.AAC_ADTS, "aac");
		file_extension.put(MediaRecorder.OutputFormat.AMR_NB, "amr");
	}

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_START_RECORD = 11;
	public static final int MSG_STOP_RECORD = 12;
	public static final int MSG_GET_STATUS = 13;
	public static final int MSG_TIME_START = 14;
	public static final int MSG_SETTINGS_UPDATED = 15;

	private void CancelAllNotif() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}

	public class MessageProto {
		public int type;
		public long value;

		public MessageProto(int t, long v) {
			type = t;
			value = v;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	private void sendObjClient(int msg, long value) {

		try {
			mClient.send(Message
					.obtain(null, msg, new MessageProto(msg, value)));
		} catch (RemoteException e) {
		}
	}

	private void sendMsgClient(int msg) {

		try {
			mClient.send(Message.obtain(null, msg, msg, 0));
		} catch (RemoteException e) {
		}
	}

	class IncomingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClient = msg.replyTo;
				break;
			case MSG_START_RECORD:
				StartRecord();
				break;
			case MSG_STOP_RECORD:
				StopRecord();
				break;
			case MSG_TIME_START:
				sendObjClient(MSG_TIME_START, StartRecord);
				break;
			case MSG_GET_STATUS:
				if (recording)
					sendMsgClient(MSG_START_RECORD);
				else
					sendMsgClient(MSG_STOP_RECORD);
				break;
			case MSG_SETTINGS_UPDATED:
				UpdatePref();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	public void onStopRecord() {
		sendMsgClient(MSG_STOP_RECORD);
		recording = false;
		StartRecord = 0;
		CancelAllNotif();
	}

	public void onStartRecord() {
		sendMsgClient(MSG_START_RECORD);
		recording = true;
		StartRecord = System.currentTimeMillis();
	}

	public void SetFilename(String name) {
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + Folder + "/");
		dir.mkdir();

		Filename = Environment.getExternalStorageDirectory().getAbsolutePath();
		Filename += "/" + Folder + "/" + name + "."
				+ file_extension.get(S_AudioFormat);
	}

	private String generateFileName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss");
		String currentDateandTime = sdf.format(new Date());

		return currentDateandTime;
	}

	private OnInfoListener MediaInfoListener = new OnInfoListener() {

		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			if ((what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
					|| (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)) {
				StopRecord();
			}
		}
	};

	private void StartRecord() {
		SetFilename(generateFileName());
		mRecord = new MediaRecorder();
		mRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecord.setOutputFile(Filename);
		mRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecord.setMaxDuration(S_MaxTimeRecord);
		mRecord.setMaxFileSize(S_MaxFileSize);
		mRecord.setOnInfoListener(MediaInfoListener);
		try {
			mRecord.prepare();

		} catch (IOException e) {
			int a = 0;
		}
		mRecord.start();
		onStartRecord();
	}

	private void StopRecord() {
		if (mRecord == null) {
			mRecord = null;
		} else {
			mRecord.stop();
			mRecord.release();
		}
		onStopRecord();
	}

	public void UpdatePref() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		S_AudioFormat = Integer.parseInt((sharedPref.getString("set_format",
				"1")));
		S_Folder = sharedPref.getString("default_folder", "RecorderGroup1024");
		S_FileName = sharedPref.getString("default_file_name",
				"RecorderGroup1024");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CancelAllNotif();

		UpdatePref();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				UpdatePref();
			}
		};
		prefs.registerOnSharedPreferenceChangeListener(listener);
	}

	@Override
	public void onDestroy() {
		CancelAllNotif();
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}
