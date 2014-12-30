package cn.edu.bjtu.group1024.recorder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.frontia.Frontia;

public class MainActivity extends Activity {

	// API Key£º7QZKIWYxikvOK4MiLiYMe2cG
	// Secret Key£º S2eyPbtdvVFkj8wDj6pZeidqzSNNkDuc
	private final static String API_KEY = "7QZKIWYxikvOK4MiLiYMe2cG";
	private final static String SECRET_KEY = "S2eyPbtdvVFkj8wDj6pZeidqzSNNkDuc";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		boolean isInit = Frontia.init(getApplicationContext(), API_KEY);
		if (isInit) {// Frontia is successfully initialized.
			// Use Frontia
			Log.d("tanshuai", "isInit = " + isInit);
		}
	}

}
