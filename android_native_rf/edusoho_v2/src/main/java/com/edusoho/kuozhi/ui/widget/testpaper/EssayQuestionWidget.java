package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Question.Answer;
import com.edusoho.kuozhi.model.Testpaper.MaterialQuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.fragment.testpaper.EssayFragment;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.listener.NormalCallback;

import java.util.ArrayList;


/**
 * Created by howzhi on 14-9-29.
 */
public class EssayQuestionWidget extends BaseQuestionWidget
        implements MessageEngine.MessageCallback {

    protected TextView stemView;
    private EditText contentEdt;
    private ImageView mPhotoBtn;
    private ImageView mCameraBtn;
    private static final int IMAGE_SIZE = 500;

    public static final int GET_PHOTO = 0001;
    public static final int GET_CAMERA = 0002;

    /**
     * 图片数量
     */
    private int mImageCount = 1;

    public EssayQuestionWidget(Context context) {
        super(context);
    }

    public EssayQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case GET_PHOTO:
            case GET_CAMERA:
                Bundle bundle = message.data;
                String filePath = bundle.getString("file");
                Log.d(null, "file------>" + filePath);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
                insertImage(contentEdt, filePath, bitmap);
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(GET_PHOTO, source),
                new MessageType(GET_CAMERA, source)
        };
        return messageTypes;
    }

    private TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            Bundle bundle = new Bundle();
            bundle.putInt("index", mIndex - 1);
            if (mQuestionSeq instanceof MaterialQuestionTypeSeq) {
                bundle.putString("QuestionType", QuestionType.material.name());
            } else {
                bundle.putString("QuestionType", mQuestionSeq.question.type.name());
            }
            ArrayList<String> data = new ArrayList<String>();
            data.add(charSequence.toString());
            bundle.putStringArrayList("data", data);
            EdusohoApp.app.sendMsgToTarget(
                    TestpaperActivity.CHANGE_ANSWER, bundle, TestpaperActivity.class);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };


    @Override
    protected void invalidateData() {
        stemView = (TextView) this.findViewById(R.id.question_stem);
        contentEdt = (EditText) this.findViewById(R.id.essay_content);
        mPhotoBtn = (ImageView) this.findViewById(R.id.essay_photo);
        mCameraBtn = (ImageView) this.findViewById(R.id.essay_camera);

        stemView.setText(getQuestionStem());
        contentEdt.addTextChangedListener(onTextChangedListener);

        mPhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EdusohoApp.app.sendMsgToTargetForCallback(
                        EssayFragment.PHOTO, null, EssayFragment.class, new NormalCallback() {
                    @Override
                    public void success(Object obj) {
                        String filePath = obj.toString();
                        Log.d(null, "file------>" + filePath);
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
                        insertImage(contentEdt, filePath, bitmap);
                    }
                });
            }
        });

        mCameraBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EdusohoApp.app.sendMsgToTargetForCallback(
                        EssayFragment.CAMERA, null, EssayFragment.class, new NormalCallback() {
                    @Override
                    public void success(Object obj) {
                        String filePath = obj.toString();
                        Log.d(null, "file------>" + filePath);
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
                        insertImage(contentEdt, filePath, bitmap);
                    }
                });
            }
        });
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    public void setData(QuestionTypeSeq questionSeq, int index) {
        super.setData(questionSeq, index);
        invalidateData();
    }

    /**
     * 光标处插入图片
     *
     * @param imageName
     * @param image
     */
    private void insertImage(EditText editText, String imageName, Bitmap image) {
        Editable eb = editText.getEditableText();
        //获得光标所在位置
        int qqPosition = editText.getSelectionStart();
        SpannableString ss = new SpannableString(String.valueOf(mImageCount++));
        if (image.getWidth() > 500) {
            image = scaleImage(image);
        }

        //定义插入图片
        Drawable drawable = new BitmapDrawable(image);
        ss.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        drawable.setBounds(2, 0, drawable.getIntrinsicWidth() + 2, drawable.getIntrinsicHeight() + 2);

        //插入图片
        eb.insert(qqPosition, ss);
    }

    /**
     * 图片缩小
     *
     * @param bitmap
     * @return
     */
    private Bitmap scaleImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = AppUtil.dip2px(mContext, IMAGE_SIZE);

        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);

        return scaledBitmap;
    }
}
