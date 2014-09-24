package com.edusoho.kuozhi.ui.question;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.SubmitResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class QuestionReplyActivity extends ActionBarBaseActivity implements View.OnClickListener {
    private static final String TAG = "QuestionReplyActivity";
    private static final int IMAGE_SIZE = 500;
//    public static final String THREAD_ID = "thread_id";
//    public static final String COURSE_ID = "course_id";

    private static final int REQUEST_CODE = 0x01;

    public static final String REQUESTI_CODE = "reply_type";
    public static final String CONTENT = "content";

    public static final int REPLY = 0x01;
    public static final int EDIT_QUESTION = 0x02;
    public static final int EDIT_REPLY = 0x03;

    public static final int RESULT_OK = 0x10;

    private EditText etContent;

    private ImageView ivBoldStyle;
    private ImageView ivItalicStyle;
    private ImageView ivUnderLineStyle;
    private ImageView ivFontColorStyle;
    private ImageView ivOrderListStyle;
    private ImageView ivUnorderListStyle;
    private ImageView ivCamera;
    private ImageView ivPhoto;
    private String mCourseId;
    private String mThreadId;

    /**
     * 记录文本选择的起始位置
     */
    private int mSelectTextStart;

    /**
     * 记录文本选择的结束位置
     */
    private int mSelectTextEnd;

    /**
     * 图片数量
     */
    private int mImageCount = 1;
    private CharacterStyle[] mCurrentStyle;
    private StyleSpan mStyleBold = new StyleSpan(Typeface.BOLD);
    private StyleSpan mStyleItalic = new StyleSpan(Typeface.ITALIC);
    private UnderlineSpan mStyleUnderline = new UnderlineSpan();

    private List<String> mImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_layout);
        setBackMode(BACK, "回复");
        initViews();
    }

    private void initViews() {
        mCourseId = getIntent().getStringExtra(QuestionDetailActivity.COURSE_ID);
        mThreadId = getIntent().getStringExtra(QuestionDetailActivity.THREAD_ID);
        etContent = (EditText) findViewById(R.id.et_content);
        etContent.setOnClickListener(this);

        ivBoldStyle = (ImageView) findViewById(R.id.iv_bold);
        ivItalicStyle = (ImageView) findViewById(R.id.iv_italic);
        ivUnderLineStyle = (ImageView) findViewById(R.id.iv_underline);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        ivBoldStyle.setOnClickListener(richTextListener);
        ivItalicStyle.setOnClickListener(richTextListener);
        ivUnderLineStyle.setOnClickListener(richTextListener);
        ivPhoto.setOnClickListener(richTextListener);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.) {
//            Intent intent = new Intent();
//            switch (getIntent().getIntExtra(QuestionReplyActivity.REQUESTI_CODE, REPLY)) {
//                case REPLY:
//                    //新增回复api
//                    break;
//                case EDIT_QUESTION:
//                    //编辑问题api
//                    break;
//                case EDIT_REPLY:
//                    //编辑回复api
//                    break;
//            }
//            intent.putExtra(QuestionReplyActivity.CONTENT, etContent.getText());
//            setResult(QuestionReplyActivity.RESULT_OK, intent);
//        }
        if (v.getId() == R.id.et_content) {
            mCurrentStyle = etContent.getText().getSpans(etContent.getSelectionStart() - 1, etContent.getSelectionEnd(),
                    CharacterStyle.class);
            setRichTextImage();
            Log.d(TAG, etContent.getSelectionStart() + "_" + etContent.getSelectionEnd());
        }
    }

    private View.OnClickListener richTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSelectTextStart = etContent.getSelectionStart();
            mSelectTextEnd = etContent.getSelectionEnd();

            if (v.getId() == R.id.iv_bold) {
                if (ivBoldStyle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon_font_bold).getConstantState())) {
                    ivBoldStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold_on_click));
                    etContent.getText().setSpan(mStyleBold, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                } else {
                    ivBoldStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold));
                    etContent.getText().removeSpan(mStyleBold);
                }
            } else if (v.getId() == R.id.iv_italic) {
                if (ivItalicStyle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon_font_italic).getConstantState())) {
                    ivItalicStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_italic_on_click));
                    etContent.getText().setSpan(mStyleItalic, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                } else {
                    ivItalicStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_italic));
                    etContent.getText().removeSpan(mStyleItalic);
                }
            } else if (v.getId() == R.id.iv_underline) {
                if (ivUnderLineStyle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon_font_under_line).getConstantState())) {
                    ivUnderLineStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_under_line_on_click));
                    etContent.getText().setSpan(mStyleUnderline, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                } else {
                    ivUnderLineStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_under_line));
                    etContent.getText().removeSpan(mStyleUnderline);
                }
            } else if (v.getId() == R.id.iv_photo) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);

            }
        }
    };

    /**
     * 获取光标所在位置的字体，并改变图标状态
     */
    private void setRichTextImage() {
        ivBoldStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold));
        ivItalicStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_italic));
        ivUnderLineStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_under_line));

        for (CharacterStyle style : mCurrentStyle) {
            if (style instanceof UnderlineSpan) {
                //下划线
                ivUnderLineStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_under_line_on_click));
            } else if (style instanceof StyleSpan) {
                if (((StyleSpan) style).getStyle() == Typeface.BOLD) {
                    //粗体
                    ivBoldStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold_on_click));
                } else if (((StyleSpan) style).getStyle() == Typeface.ITALIC) {
                    //倾斜
                    ivItalicStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_italic_on_click));
                }
            }
        }
    }

    private String filename;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Uri uri = data.getData();
                //根据需要，也可以加上Option这个参数
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                filename = data.getDataString();
                int tmpLen = data.getDataString().split("/").length;
                String fileName = data.getDataString().split("/")[tmpLen - 1];

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                insertImage(fileName, bitmap);
            }
        }
    }

    /**
     * 光标处插入图片
     *
     * @param imageName
     * @param image
     */
    private void insertImage(String imageName, Bitmap image) {
        Editable eb = etContent.getEditableText();
        //获得光标所在位置
        int qqPosition = etContent.getSelectionStart();
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
        int bounding = dpToPx(IMAGE_SIZE);

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

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.question_reply_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reply_submit) {
            submitReply();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 回复提交
     */
    private void submitReply() {
        //Log.e(TAG, Html.toHtml(etContent.getText()).toString());
        RequestUrl url = app.bindUrl(Const.REPLY_SUBMIT, true);
        url.setMuiltParams(new Object[]{"image1", new File(filename)});
        HashMap<String, String> params = url.getParams();
        params.put("courseId", mCourseId);
        params.put("threadId", mThreadId);
        params.put("content", Html.toHtml(etContent.getText()));
        params.put("imageCount", "1");

        this.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    SubmitResult submitResult = mActivity.gson.fromJson(object, new TypeToken<SubmitResult>() {
                    }.getType());
                    if (submitResult == null) {
                        return;
                    } else {
                        Toast.makeText(mContext, "提交成功", 500).show();
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != 200) {
                    Log.e(TAG, String.valueOf(ajaxStatus.getCode()));
                }
            }
        });
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (etContent.hasFocus()) {
            mCurrentStyle = etContent.getText().getSpans(etContent.getSelectionStart(), etContent.getSelectionEnd(),
                    CharacterStyle.class);
            //setRichTextImage();
            Log.d(TAG, etContent.getSelectionStart() + "_" + etContent.getSelectionEnd());
        }
        return super.onKeyUp(keyCode, event);
    }
}
