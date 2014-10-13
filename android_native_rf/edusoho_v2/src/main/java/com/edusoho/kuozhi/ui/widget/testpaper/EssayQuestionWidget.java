package com.edusoho.kuozhi.ui.widget.testpaper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Question.Answer;
import com.edusoho.kuozhi.model.Testpaper.MaterialQuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.PaperResult;
import com.edusoho.kuozhi.model.Testpaper.Question;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.model.Testpaper.TestResult;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.fragment.testpaper.EssayFragment;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;
import com.edusoho.kuozhi.ui.lesson.TestpaperParseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EduSohoTextBtn;
import com.edusoho.listener.NormalCallback;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.ImageUtils;


/**
 * Created by howzhi on 14-9-29.
 */
public class EssayQuestionWidget extends BaseQuestionWidget
        implements MessageEngine.MessageCallback {

    protected TextView stemView;
    private EditText contentEdt;
    private ImageView mPhotoBtn;
    private ImageView mCameraBtn;
    private View mToolsLayout;

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
        super.invalidateData();

        mToolsLayout = this.findViewById(R.id.essay_tools_layout);
        stemView = (TextView) this.findViewById(R.id.question_stem);
        contentEdt = (EditText) this.findViewById(R.id.essay_content);
        mPhotoBtn = (ImageView) this.findViewById(R.id.essay_photo);
        mCameraBtn = (ImageView) this.findViewById(R.id.essay_camera);

        stemView.setText(getQuestionStem());
        contentEdt.addTextChangedListener(onTextChangedListener);

        mPhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TestpaperActivity testpaperActivity = TestpaperActivity.getInstance();
                testpaperActivity.setType(TestpaperActivity.PHOTO_CAMEAR);
                EdusohoApp.app.sendMsgToTargetForCallback(
                        EssayFragment.PHOTO, null, EssayFragment.class, new NormalCallback() {
                    @Override
                    public void success(Object obj) {
                        addImageToEdit((Bundle) obj);
                    }
                });
            }
        });

        mCameraBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TestpaperActivity testpaperActivity = TestpaperActivity.getInstance();
                testpaperActivity.setType(TestpaperActivity.PHOTO_CAMEAR);
                EdusohoApp.app.sendMsgToTargetForCallback(
                        EssayFragment.CAMERA, null, EssayFragment.class, new NormalCallback() {
                    @Override
                    public void success(Object obj) {
                        addImageToEdit((Bundle) obj);
                    }
                });
            }
        });

        if (mQuestion.testResult != null) {
            contentEdt.setVisibility(GONE);
            mToolsLayout.setVisibility(GONE);
            mAnalysisVS = (ViewStub) this.findViewById(R.id.quetion_choice_analysis);
            mAnalysisVS.setOnInflateListener(new ViewStub.OnInflateListener() {
                @Override
                public void onInflate(ViewStub viewStub, View view) {
                    initResultAnalysis(view);
                }
            });
            mAnalysisVS.inflate();
        }
    }

    @Override
    protected void initResultAnalysis(View view)
    {
        TextView myAnswerText = (TextView) view.findViewById(R.id.question_my_anwer);
        TextView myRightText = (TextView) view.findViewById(R.id.question_right_anwer);
        TextView AnalysisText = (TextView) view.findViewById(R.id.question_analysis);

        TestResult testResult = mQuestion.testResult;
        String myAnswer = null;
        if ("noAnswer".equals(testResult.status)) {
            myAnswer = "未答题";
        } else {
            myAnswer = listToStr(testResult.answer);
        }

        String html = "你的答案:<p></p>" + myAnswer;
        myAnswerText.setText(Html.fromHtml(
                html, new NetImageGetter(myAnswerText, html), null));

        html = "参考答案:<p></p>" + listToStr(mQuestion.answer);
        myRightText.setText(Html.fromHtml(
                html, new NetImageGetter(myRightText, html), null));

        AnalysisText.setText(Html.fromHtml(
                mQuestion.analysis, new NetImageGetter(AnalysisText, mQuestion.analysis), null));

        initFavoriteBtn(view);

        TestpaperParseActivity testpaperParseActivity = TestpaperParseActivity.getInstance();
        if (testpaperParseActivity == null) {
            return;
        }

        TextView readLabel = (TextView) view.findViewById(R.id.question_read_label);
        PaperResult paperResult = testpaperParseActivity.getPaperResult();
        if ("finished".equals(paperResult.status)) {
            readLabel.setVisibility(GONE);
        } else {
            readLabel.setVisibility(VISIBLE);
        }
    }

    private class NetImageGetter implements Html.ImageGetter
    {
        private TextView mTextView;
        private String html;

        public NetImageGetter(TextView textView, String html)
        {
            this.html = html;
            mTextView = textView;
        }

        @Override
        public Drawable getDrawable(String s) {
            Drawable drawable = null;
            AQuery aQuery = new AQuery(mContext);
            File cacheDir = AQUtility.getCacheDir(mContext);
            String fileName = DigestUtils.md5(s);

            File file = new File(cacheDir, fileName);
            Log.d(null, "update file->" + file);
            if (file != null && file.exists()) {
                Bitmap bitmap = AppUtil.getBitmapFromFile(file);
                drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }

            try {
                file.createNewFile();
            }catch (Exception e) {
                e.printStackTrace();
            }
            aQuery.download(s, file, new AjaxCallback<File>() {
                @Override
                public void callback(String url, File object, AjaxStatus status) {
                    super.callback(url, object, status);
                    mTextView.setText(Html.fromHtml(html, new NetImageGetter(mTextView, html), null));
                }
            });
            return drawable;
        }
    }

    private void addImageToEdit(Bundle bundle) {
        String filePath = bundle.getString("file");
        String imageTag = bundle.getString("image");
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
        insertImage(contentEdt, filePath, bitmap, imageTag);

        Log.d(null, "edit->" + contentEdt.getText().toString());
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
    private void insertImage(EditText editText, String imageName, Bitmap image, String imageTag) {
        Editable eb = editText.getEditableText();
        //获得光标所在位置
        int qqPosition = editText.getSelectionStart();
        SpannableString ss = new SpannableString(imageTag);
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
