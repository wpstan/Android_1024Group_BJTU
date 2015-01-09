package cn.edu.bjtu.group1024.recorder.activity;

import cn.edu.bjtu.group1024.recorder.R;
import cn.edu.bjtu.group1024.recorder.R.xml;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity {

	public static final String activity_title = "设置";

	public void updateTheme() {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (sharedPref.getBoolean("use_dark_theme", false))
			setTheme(android.R.style.Theme_Holo);
		else
			setTheme(android.R.style.Theme_Holo_Light);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public static class SettingsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
		}

		@Override
		public void onPause() {
			super.onPause();
			getPreferenceManager().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences arg0,
				String arg1) {
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateTheme();
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(activity_title);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}

}
