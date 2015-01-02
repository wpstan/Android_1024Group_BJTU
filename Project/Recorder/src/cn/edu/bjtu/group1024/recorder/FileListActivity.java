package cn.edu.bjtu.group1024.recorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class FileListActivity extends ListActivity {

	public static final String activity_title = "录音记录";
	Context context;

	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/sdcard/RecorderGroup1024/";

	MediaPlayer mediaPlayer;

	public void updateTheme() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sharedPref.getBoolean("use_dark_theme", false))
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		updateTheme();
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(activity_title);

		setContentView(R.layout.activity_filelist);
		this.getFileDir(rootPath);

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				ListLongClick((ListView) arg0, arg1, arg2, arg3);
				return false;
			}
		});
	}

	public void getFileDir(String filePath) {
		try {
			items = new ArrayList<String>();
			paths = new ArrayList<String>();
			File f = new File(filePath);
			File[] files = f.listFiles();

			if (!filePath.equals(rootPath)) {
				items.add("uproot");
				paths.add(rootPath);
				items.add("up level");
				paths.add(f.getParent());
			}

			if (files != null) {
				int count = files.length;
				for (int i = 0; i < count; i++) {
					File file = files[i];
					items.add(file.getName());
					paths.add(file.getPath());
				}
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, items);
			this.setListAdapter(adapter);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	protected void ListLongClick(ListView l, View v, int position, long id) {
		String[] menu = { "播放", "重命名", "删除", "分享", "设为铃声" };
		final String path = paths.get(position);
		final File file = new File(path);
		OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 4) {
					setMyRingtone(path);
				} else if (which == 2) {
					file.delete();
					getFileDir(rootPath);
				} else if (which == 1) {
					renameDialog(path);
				} else if (which == 3) {
					share(path);
				} else if (which == 0) {
					mediaPlayer = new MediaPlayer();
					try {
						mediaPlayer.setDataSource(path);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						mediaPlayer.prepare();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mediaPlayer.start();
					new AlertDialog.Builder(FileListActivity.this)
							.setTitle("正在播放")
							.setMessage(file.getName() + " 正在播放...")
							.setPositiveButton("停止",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											mediaPlayer.stop();
											mediaPlayer.release();
										}

									}).show();
				}
			}
		};

		new AlertDialog.Builder(FileListActivity.this).setTitle("操作")
				.setItems(menu, listener).show();
	}

	private void share(String path) {
		setTransFile(path);
	}

	private void setTransFile(String path) {
		Uri fileUri = Uri.fromFile(new File(path));
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
		shareIntent.setType("*/*");
		startActivity(Intent.createChooser(shareIntent,
				getResources().getText(R.string.send_to)));
	}

	private void renameDialog(String path) {

		final EditText inputServer = new EditText(this);
		final String pathOld = path;
		inputServer.setFocusable(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("重命名").setIcon(android.R.drawable.ic_dialog_alert)
				.setView(inputServer).setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String inputName = inputServer.getText().toString();
				inputName = "/sdcard/RecorderGroup1024/" + inputName;
				File oleFile = new File(pathOld);
				File newFile = new File(inputName);
				oleFile.renameTo(newFile);
				getFileDir(rootPath);
			}
		});
		builder.show();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String path = paths.get(position);
		File file = new File(path);
		//
		if (file.isDirectory()) {
			this.getFileDir(path);
		} else {
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(path);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mediaPlayer.start();

			new AlertDialog.Builder(this)
					.setTitle("正在播放")
					.setMessage(file.getName() + " 正在播放...")
					.setPositiveButton("停止",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									mediaPlayer.stop();
									mediaPlayer.release();

								}

							}).show();
		}
	}

	public String getfile() {
		File file = new File("/sdcard/RecorderGroup1024/");
		int len = file.list().length - 1;
		String lastfile = "";
		long time = 0;
		for (; len >= 0; len--) {
			if (time < file.listFiles()[len].lastModified()) {
				time = file.listFiles()[len].lastModified();
				lastfile = file.list()[len];
			}
		}

		return "/sdcard/RecorderGroup1024/" + lastfile;
	}

	public void setMyRingtone(String path) {
		File sdfile = new File(path);
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);

		Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile
				.getAbsolutePath());
		Uri newUri = this.getContentResolver().insert(uri, values);
		RingtoneManager.setActualDefaultRingtoneUri(FileListActivity.this,
				RingtoneManager.TYPE_RINGTONE, newUri);
		Toast.makeText(this, "铃声设置成功!", Toast.LENGTH_SHORT).show();
	}

}
