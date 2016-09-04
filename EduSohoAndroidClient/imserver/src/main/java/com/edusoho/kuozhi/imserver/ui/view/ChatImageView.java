package com.edusoho.kuozhi.imserver.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by suju on 16/9/2.
 */
public class ChatImageView extends ImageView {

    private int mBackgroudRes;

    public ChatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
    }

    protected void setXfermodeImage(Bitmap original) {
        Bitmap result = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        //获取遮罩层图片
        Bitmap mask = getBitmap(original.getWidth(), original.getHeight());
        //将遮罩层的图片放到画布中
        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mCanvas.drawBitmap(original, 0, 0, null);

        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        super.setImageBitmap(result);
    }

    public void setBackgroudRes(int backgroudRes) {
        this.mBackgroudRes = backgroudRes;
    }

    private Bitmap getBitmap(int w, int h) {
        Bitmap resizedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Bitmap bmp_9 = BitmapFactory.decodeResource(getResources(), mBackgroudRes);
        Canvas canvas = new Canvas(resizedBitmap);
        NinePatch np = new NinePatch(bmp_9, bmp_9.getNinePatchChunk(), null);
        Rect rect = new Rect(0, 0, w, h);
        np.draw(canvas, rect);

        return resizedBitmap;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setXfermodeImage(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
