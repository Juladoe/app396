package com.edusohoapp.app.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.edusohoapp.app.R;


@SuppressLint("DrawAllocation")
public class EduSohoShapeView extends ImageView{

    private String nameSpace = "android";

	private Context mContext;
    private int mSrc;

	public EduSohoShapeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
        mSrc = attrs.getAttributeResourceValue(nameSpace, "src", R.drawable.course_teacher_avatar);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), mSrc);

		Bitmap temp = roundBitmap(bm, dbToPx(mContext, 70), dbToPx(mContext, 70), 10.0f);
		canvas.drawBitmap(temp, 0, 0, null);
	}

	public static int dbToPx(Context context, int dbValue)
	{
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dbValue * density);
	}

	public static Bitmap roundBitmap(Bitmap bitmap, int width, int height, float round)
	{
		Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas cancas = new Canvas(temp);

		Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Rect rect = new Rect(0, 0, width, height);
		RectF rectf = new RectF(0, 0, width, height);
		Paint paint = new Paint();
		
		paint.setAlpha(0);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);

		cancas.drawRoundRect(rectf, round, round, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		cancas.drawBitmap(bitmap, src, rect, paint);
		return temp;
	}
}
