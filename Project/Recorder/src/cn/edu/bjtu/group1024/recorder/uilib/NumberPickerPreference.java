package cn.edu.bjtu.group1024.recorder.uilib;

import cn.edu.bjtu.group1024.recorder.R;
import cn.edu.bjtu.group1024.recorder.R.xml;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class NumberPickerPreference extends DialogPreference {
	
	    public NumberPickerPreference(Context context, AttributeSet attrs) {
	        super(context, attrs);
	     
	        setDialogLayoutResource(R.xml.dialog);
	        setPositiveButtonText(android.R.string.ok);
	        setNegativeButtonText(android.R.string.cancel);

	       
	        setDialogIcon(null);
	        
	    }
	    
	}