package cn.edu.bjtu.custom.ui.uilib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import cn.edu.bjtu.custom.ui.R;

public class Group1024CustomView extends View {

	private Paint mPaint;
	private int mColor;
	private int mBitmap;

	public Group1024CustomView(Context context) {
		super(context);
	}

	public Group1024CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.customUI);
		mColor = getResources().getColor(
				a.getResourceId(R.styleable.customUI_background,
						R.color.default_color));// Ä¬ÈÏÖÃÎª°×É«
		mBitmap = a.getResourceId(R.styleable.customUI_img,
				R.drawable.ic_launcher);
		mPaint.setColor(mColor);
		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(mColor);
		canvas.drawBitmap(
				BitmapFactory.decodeResource(getResources(), mBitmap), 50, 50,
				mPaint);
	}

}
