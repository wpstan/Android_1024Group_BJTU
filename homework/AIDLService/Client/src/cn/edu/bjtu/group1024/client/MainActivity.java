package cn.edu.bjtu.group1024.client;

import cn.edu.bjtu.group1024.common.aidl.IFibonacci;
import cn.edu.bjtu.group1024.common.aidl.Request;
import cn.edu.bjtu.group1024.common.aidl.Response;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button mJavaIterationBtn;
	private Button mJavaRecursionBtn;
	private Button mNativeIterationBtn;
	private Button mNativeRecursionBtn;
	private EditText mEditText;
	private TextView mTextView;
	private Toast mToast;

	private boolean mIsCalc = false;
	private Response mResponse;

	private IFibonacci mFibonacci;
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mFibonacci = IFibonacci.Stub.asInterface(service);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mJavaIterationBtn = (Button) findViewById(R.id.btn_java_iteration);
		mJavaRecursionBtn = (Button) findViewById(R.id.btn_java_recursion);
		mNativeIterationBtn = (Button) findViewById(R.id.btn_native_iteration);
		mNativeRecursionBtn = (Button) findViewById(R.id.btn_native_recursion);
		mEditText = (EditText) findViewById(R.id.editText);
		mTextView = (TextView) findViewById(R.id.tv_consume_time);

		mJavaIterationBtn.setOnClickListener(this);
		mJavaRecursionBtn.setOnClickListener(this);
		mNativeIterationBtn.setOnClickListener(this);
		mNativeRecursionBtn.setOnClickListener(this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent("android.intent.action.FibonacciService");
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unbindService(mServiceConnection);
	}

	@Override
	public void onClick(View v) {
		if (mIsCalc) {
			showToast("请等待上一个计算返回结果...");
			return;
		}
		String editString = mEditText.getText().toString();
		int num;
		if (TextUtils.isEmpty(editString)) {
			showToast("输入为空");
			return;
		} else {
			try {
				num = Integer.parseInt(editString);
			} catch (NumberFormatException e) {
				showToast("数字太大");
				return;
			}
			if (Integer.parseInt(editString) == 0) {
				showToast("输入不能为0");
				return;
			}
		}
		final Request request = new Request();
		request.setNum(num);
		mIsCalc = true;

		switch (v.getId()) {
		case R.id.btn_java_iteration:
			new Thread() {
				public void run() {
					try {
						mResponse = mFibonacci.javaInterative(request);
						updateTextView();
					} catch (RemoteException e) {
						showToast("远程调用出错（数据太大，可能栈溢出）");
						mIsCalc = false;
					}
				};
			}.start();
			break;
		case R.id.btn_java_recursion:
			new Thread() {
				public void run() {
					try {
						mResponse = mFibonacci.javaRecursion(request);
						updateTextView();
					} catch (RemoteException e) {
						showToast("远程调用出错（数据太大，可能栈溢出）");
						mIsCalc = false;
					}
				};
			}.start();

			break;
		case R.id.btn_native_iteration:
			new Thread() {
				public void run() {
					try {
						mResponse = mFibonacci.nativeInterative(request);
						updateTextView();
					} catch (RemoteException e) {
						showToast("远程调用出错（数据太大，可能栈溢出）");
						mIsCalc = false;
					}
				};
			}.start();
			break;
		case R.id.btn_native_recursion:
			new Thread() {
				public void run() {
					try {
						mResponse = mFibonacci.nativeRecursion(request);
						updateTextView();
					} catch (RemoteException e) {
						showToast("远程调用出错（数据太大，可能栈溢出）");
						mIsCalc = false;
					}
				};
			}.start();
			break;
		}
	}

	private void updateTextView() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(mResponse.getMillSecond() + "ms ,结果为："
						+ mResponse.getmResult());
			}
		});
		mIsCalc = false;
	}

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}
}
