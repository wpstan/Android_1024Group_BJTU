package cn.edu.bjtu.group1024.recorder.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import cn.edu.bjtu.group1024.recorder.R;
import cn.edu.bjtu.group1024.recorder.uilib.PullRefreshListView;
import cn.edu.bjtu.group1024.recorder.uilib.PullRefreshListView.OnRefreshListener;
import cn.edu.bjtu.group1024.recorder.utils.PreferenceUtils;

import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.baidu.pcs.BaiduPCSActionInfo.PCSFileFromToResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSListInfoResponse;
import com.baidu.pcs.BaiduPCSActionInfo.PCSSimplefiedResponse;
import com.baidu.pcs.BaiduPCSClient;
import com.baidu.pcs.BaiduPCSStatusListener;

public class FileListActivity extends ListActivity {

	public static final String activity_title = "录音记录";
	private final int CLOUD_SHOW = 1024;
	private final int LOCAL_SHOW = 1025;
	Context context;

	private boolean mIsLogin;
	private String mUserName;
	BaiduPCSClient mBaiduClient = new BaiduPCSClient();
	private String mToken;
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/sdcard/RecorderGroup1024/";
	private final static String BAIDU_ROOT_PATH = "/apps/pcstest_oauth/";
	private ProgressDialog mBaiduUploadDialog;
	private List<PCSCommonFileInfo> mBaiduFileList;// 云端数据列表

	private ArrayAdapter<String> mLocalAdapter;
	private ArrayAdapter<String> mCloudAdapter;
	private Menu mMenu;
	private int mWhichMenuShow;
	private PullRefreshListView mCloudListView;

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

		Intent intent = getIntent();
		mIsLogin = intent.getBooleanExtra("islogin", false);
		mUserName = intent.getStringExtra("username");
		mBaiduUploadDialog = new ProgressDialog(this);
		mBaiduUploadDialog.setTitle("上传中");
		mBaiduUploadDialog.setMessage("上传数据中");
		mBaiduUploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		updateTheme();
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(activity_title);

		setContentView(R.layout.activity_filelist);
		this.getFileDir(rootPath);// 本地的adapter
		if (mIsLogin) {
			mToken = PreferenceUtils.getToken();
			mBaiduClient.setAccessToken(mToken);
			getCloudFile();// 百度云的adapter
		}

		mCloudListView = (PullRefreshListView) findViewById(R.id.cloud_listview);
		mCloudListView.setAdapter(mCloudAdapter);
		mCloudListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getCloudFile();
			}
		});

		// 百度云列表的点击事件
		mCloudListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				String[] menu = { "云端重命名", "从云端删除", "下载到本地" };

				new AlertDialog.Builder(FileListActivity.this).setTitle("操作")
						.setItems(menu, new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									// 重命名云端
									renameBaiduDialog(position);
								} else if (which == 1) {
									// 删除云端
									deleteBaiduFile(position);
								} else if (which == 2) {
									// 下载云端数据到本地
									downloadBaiduFile(position);
								}
							}
						}).show();
			}
		});
	}

	private void getCloudFile() {
		new Thread() {
			@Override
			public void run() {
				PCSListInfoResponse response = mBaiduClient.list(
						BAIDU_ROOT_PATH, "time", "desc");
				mBaiduFileList = response.list;
				final String[] itemStrings = new String[mBaiduFileList.size()];
				for (int i = 0; i < mBaiduFileList.size(); i++) {
					itemStrings[i] = mBaiduFileList.get(i).path.substring(20);
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mCloudAdapter = new ArrayAdapter<String>(
								FileListActivity.this,
								android.R.layout.simple_list_item_1,
								itemStrings);
						mCloudListView.setAdapter(mCloudAdapter);
						mCloudListView.onRefreshComplete();
						mCloudAdapter.notifyDataSetChanged();
					}
				});
				Log.d("tanshuai", " 获取文件列表：" + response.status.errorCode + "  ");
			}
		}.start();
	}

	private void getFileDir(String filePath) {
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

			mLocalAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, items);
			this.setListAdapter(mLocalAdapter);
			mLocalAdapter.notifyDataSetChanged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

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

	// 下载百度云端文件
	private void downloadBaiduFile(final int cloudFileIndex) {
		new Thread() {
			public void run() {
				final PCSSimplefiedResponse response = mBaiduClient
						.downloadFile(
								mBaiduFileList.get(cloudFileIndex).path,
								rootPath
										+ mBaiduFileList.get(cloudFileIndex).path
												.substring(20));
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						String toastMsg;
						if (response.errorCode == 0) {
							toastMsg = "下载成功";
							getCloudFile();// 下载成功刷新listview
						} else {
							toastMsg = response.message;
						}
						Toast.makeText(FileListActivity.this, toastMsg,
								Toast.LENGTH_SHORT).show();

					}
				});
			};
		}.start();
	}

	// 删除百度云端的文件
	private void deleteBaiduFile(final int cloudFileIndex) {
		new Thread() {
			public void run() {
				final PCSSimplefiedResponse response = mBaiduClient
						.deleteFile(mBaiduFileList.get(cloudFileIndex).path);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						String toastMsg;
						if (response.errorCode == 0) {
							toastMsg = "删除成功";
							getCloudFile();// 删除成功刷新listview
						} else {
							toastMsg = response.message;
						}
						Toast.makeText(FileListActivity.this, toastMsg,
								Toast.LENGTH_SHORT).show();

					}
				});
			};
		}.start();

	}

	// 重命名百度云端的文件
	private void renameBaiduDialog(final int cloudFileIndex) {

		final EditText inputServer = new EditText(this);
		inputServer.setFocusable(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("重命名").setIcon(android.R.drawable.ic_dialog_alert)
				.setView(inputServer).setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				new Thread() {
					@Override
					public void run() {
						final PCSFileFromToResponse response = mBaiduClient.rename(
								mBaiduFileList.get(cloudFileIndex).path,
								inputServer.getText().toString());
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								String toastMsg;
								if (response.status.errorCode == 0) {
									toastMsg = "重命名成功";
									getCloudFile();// 重命名成功刷新listview
								} else {
									toastMsg = response.status.message;
									// "重命名失败";
								}
								Toast.makeText(FileListActivity.this, toastMsg,
										Toast.LENGTH_SHORT).show();
							}
						});
					}
				}.start();
				return;// 当云端点击重命名
			}
		});
		builder.show();

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

	// 本地列表长按事件
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int menuLength;
		if (mIsLogin) {
			menuLength = 6;
		} else {
			menuLength = 5;
		}
		String[] menu = new String[menuLength];
		menu[0] = "播放";
		menu[1] = "重命名";
		menu[2] = "删除";
		menu[3] = "分享";
		menu[4] = "设为铃声";
		if (mIsLogin) {
			menu[5] = "上传到百度云";
		}
		final String path = paths.get(position);
		final File file = new File(path);
		OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface d, int which) {
				if (which == 5) {
					mBaiduUploadDialog.show();
					new Thread() {
						public void run() {
							Log.d("tanshuai", "传输前");
							final PCSFileInfoResponse fileResponse = mBaiduClient
									.uploadFile(path,
											BAIDU_ROOT_PATH + file.getName(),
											new BaiduPCSStatusListener() {

												@Override
												public void onProgress(
														long progress,
														long total) {
													final int percent = ((int) (progress / total)) * 100;
													runOnUiThread(new Runnable() {
														public void run() {
															mBaiduUploadDialog
																	.setProgress(percent);
														}
													});
													Log.d("tanshuai", "进度:"
															+ progress + " / "
															+ total);
												}
											});
							runOnUiThread(new Runnable() {
								public void run() {
									mBaiduUploadDialog.cancel();

									String toastMsg;
									if (fileResponse.status.errorCode == 0) {
										toastMsg = "传输成功";
									} else {
										toastMsg = fileResponse.status.message
												.toString();
									}
									Toast.makeText(FileListActivity.this,
											toastMsg, Toast.LENGTH_SHORT)
											.show();

								}
							});
						};
					}.start();
				} else if (which == 4) {
					setMyRingtone(path);
				} else if (which == 2) {
					file.delete();
					getFileDir(rootPath);
				} else if (which == 1) {
					renameDialog(path);
				} else if (which == 3) {
					share(path);
				} else if (which == 0) {
					final AlertDialog playDialog = new AlertDialog.Builder(
							FileListActivity.this)
							.setTitle("正在播放")
							.setMessage(file.getName() + " 正在播放...")
							.setPositiveButton("停止",
									new DialogInterface.OnClickListener() {

										public void onClick(DialogInterface d,
												int which) {
											mediaPlayer.stop();
											mediaPlayer.release();
										}

									}).show();

					mediaPlayer = new MediaPlayer();
					mediaPlayer
							.setOnCompletionListener(new OnCompletionListener() {

								@Override
								public void onCompletion(MediaPlayer mp) {
									playDialog.dismiss();
								}
							});
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

				}
			}
		};

		new AlertDialog.Builder(FileListActivity.this).setTitle("操作")
				.setItems(menu, listener).show();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_local:
			mCloudListView.setVisibility(View.GONE);
			getListView().setVisibility(View.VISIBLE);
			mWhichMenuShow = LOCAL_SHOW;
			getFileDir(rootPath);
			item.setEnabled(false);
			mMenu.getItem(1).setEnabled(true);
			return true;
		case R.id.action_cloud:
			mCloudListView.setVisibility(View.VISIBLE);
			getListView().setVisibility(View.GONE);
			mWhichMenuShow = CLOUD_SHOW;
			item.setEnabled(false);
			mMenu.getItem(0).setEnabled(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.history, menu);
		menu.getItem(0).setEnabled(false);
		if (!mIsLogin) {
			menu.getItem(1).setEnabled(false);
		}
		mMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}
}
