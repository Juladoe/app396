package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.ActivityUtil;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tree on 2017/4/28.
 */

public class CouponDetailActivity extends ActionBarBaseActivity {

    private TextView mTitle;
    private TextView mName;
    private TextView mDeadline;
    private TextView mCode;
    private TextView mUserRule;
    private ImageView mQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R. layout.activity_voucher_detail);
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        ActivityUtil.setStatusViewBackgroud(this,Color.BLACK);
        setBackMode(null,"卡券详情");
        initView();
        initData();
    }

    private void initView() {
        mTitle = (TextView) mActionBar.getCustomView().findViewById(R.id.tv_action_bar_title);
        mTitle.setTextColor(getResources().getColor(R.color.primary_font_color));
        mName = (TextView) findViewById(R.id.tv_name);
        mDeadline = (TextView) findViewById(R.id.tv_deadline);
        mCode = (TextView) findViewById(R.id.tv_code);
        mUserRule = (TextView) findViewById(R.id.tv_use_rule);
        mQrCode = (ImageView) findViewById(R.id.iv_qrcode);
    }

    private void initData(){
        Intent data = getIntent();
        if(data != null){
            String name = data.getStringExtra(MyCardPackActivity.COUPON_NAME);
            String price = data.getStringExtra(MyCardPackActivity.COUPON_PRICE);
            String deadline = data.getStringExtra(MyCardPackActivity.COUPON_DEADLINE);
            String code = data.getStringExtra(MyCardPackActivity.COUPON_QRCODE);
            String url = data.getStringExtra(MyCardPackActivity.COUPON_URL);
            String desc = data.getStringExtra(MyCardPackActivity.COUPON_DESC);
            mName.setText((int)Double.parseDouble(price) + "元" + name);
            mDeadline.setText("有效期至" + getDeadline(deadline));
            mUserRule.setText(desc);
            mCode.setText(code);
            Bitmap bitmap = createQRCodeBitmap(url, AppUtil.dp2px(mContext, 150), AppUtil.dp2px(mContext, 150));
            mQrCode.setImageBitmap(bitmap);
        }
    }

    private String getDeadline(String time){
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    private Bitmap createQRCodeBitmap(String content,int width, int height) {
        if(TextUtils.isEmpty(content)){
            return null;
        }
        if(width < 0 || height < 0){
            return null;
        }
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            BitMatrix matrix  = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            matrix = deleteWhite(matrix);//删除白边
            width = matrix.getWidth();
            height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (matrix .get(j, i)) {
                        pixels[i * width + j] = Color.BLACK;
                    } else {
                        pixels[i * width + j] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }

}
