package com.edusoho.kuozhi.ui.question;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.SubmitResult;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class QuestionReplyActivity extends ActionBarBaseActivity implements View.OnClickListener {

    private static final String TAG = "QuestionReplyActivity";
    private static final int IMAGE_SIZE = 500;
    /**
     * 从手机图库中选择图片返回结果表示
     */
    private static final int IMAGE_RESULT = 1;

    private static final int CAMERA_RESULT = 2;

    /**
     * 图片命名
     */
    private static final String IMAGE_NAME = "image|";

    private SortedMap<String, Object> mImageHashMap = new TreeMap<String, Object>();

    /**
     * 图片数量
     */
    private int mImageCount = 1;

    private int mCameraIndex = 1;

    private Object[] mObjects;

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
     * id
     */
    private String mReplyId;
    private String mOriginalContent;

    /**
     * 记录文本选择的起始位置
     */
    private int mSelectTextStart;

    /**
     * 记录文本选择的结束位置
     */
    private int mSelectTextEnd;


    private CharacterStyle[] mCurrentStyle;
    private StyleSpan mStyleBold = new StyleSpan(Typeface.BOLD);
    private StyleSpan mStyleItalic = new StyleSpan(Typeface.ITALIC);
    private UnderlineSpan mStyleUnderline = new UnderlineSpan();

    private List<String> mImageList;

    private int mTypeCode;
    private AQuery mAQuery;
    private Context mContext;
    private String mHost = "";
    private File mCameraImageFile;

    private ProgressDialog mProgressDialog;

    private String filename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_reply_layout);
        setBackMode(BACK, "回复");
        mContext = this;
        initViews();
        initProgressDialog();
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {
        mHost = this.app.host;
        mAQuery = new AQuery(this);
        etContent = (EditText) findViewById(R.id.et_content);
        ivBoldStyle = (ImageView) findViewById(R.id.iv_bold);
        ivItalicStyle = (ImageView) findViewById(R.id.iv_italic);
        ivUnderLineStyle = (ImageView) findViewById(R.id.iv_underline);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        ivCamera = (ImageView) findViewById(R.id.iv_camera);

        mCourseId = getIntent().getStringExtra(Const.COURSE_ID);
        mThreadId = getIntent().getStringExtra(Const.THREAD_ID);
        mTypeCode = getIntent().getIntExtra(Const.REQUEST_CODE, 0);
        if (mTypeCode == Const.EDIT_QUESTION) {
            mOriginalContent = getIntent().getStringExtra(Const.QUESTION_CONTENT);
            etContent.setText(Html.fromHtml(mOriginalContent));
        } else if (mTypeCode == Const.EDIT_REPLY) {
            mReplyId = getIntent().getStringExtra(Const.REPLY_ID);
            mOriginalContent = getIntent().getStringExtra(Const.NORMAL_CONTENT);
            etContent.setText(Html.fromHtml(mOriginalContent, imgGetter, null));
        } else if (mTypeCode == Const.REPLY) {
            mReplyId = "";
        }

        etContent.setOnClickListener(this);
        ivBoldStyle.setOnClickListener(richTextListener);
        ivItalicStyle.setOnClickListener(richTextListener);
        ivUnderLineStyle.setOnClickListener(richTextListener);
        ivPhoto.setOnClickListener(richTextListener);
        ivCamera.setOnClickListener(richTextListener);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.et_content) {
            mCurrentStyle = etContent.getText().getSpans(etContent.getSelectionStart() - 1, etContent.getSelectionEnd(),
                    CharacterStyle.class);
            setRichTextImage();
            Log.d(TAG, etContent.getSelectionStart() + "_" + etContent.getSelectionEnd());
        }
    }

    @SuppressWarnings("ConstantConditions")
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
                startActivityForResult(intent, IMAGE_RESULT);
            } else if (v.getId() == R.id.iv_camera) {
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    String saveDir = Environment.getExternalStorageDirectory().getPath() + "/temp_image";
                    mCameraImageFile = new File(saveDir, "caremaImage" + mCameraIndex + ".jpg");
                    mCameraIndex++;
                    if (!mCameraImageFile.exists()) {
                        try {
                            mCameraImageFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(QuestionReplyActivity.this, "照片创建失败!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraImageFile));
                    startActivityForResult(intent, CAMERA_RESULT);
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_RESULT:
                if (null != data) {
                    Uri uri = data.getData();
                    //根据需要，也可以加上Option这个参数
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    String filePath = convertUriToPath(data.getDataString());

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    insertImage(bitmap, filePath);
                }
                break;
            case CAMERA_RESULT:
                if (resultCode == RESULT_OK) {
                    if (mCameraImageFile != null & mCameraImageFile.exists()) {
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(mCameraImageFile.getPath(), option);
                        insertImage(bitmap, mCameraImageFile.getPath());
                    }
                }
                break;
        }
    }

    private int getImageDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return degree;
    }

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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE) {
//            if (null != data) {
//                Uri uri = data.getData();
//                //根据需要，也可以加上Option这个参数
//                InputStream inputStream = null;
//                try {
//                    inputStream = getContentResolver().openInputStream(uri);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                filename = data.getDataString();
//                int tmpLen = data.getDataString().split("/").length;
//                String fileName = data.getDataString().split("/")[tmpLen - 1];
//
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                insertImage(fileName, bitmap);
//            }
//        }
//    }

    /**
     * 光标处插入图片
     *
     * @param image
     */
    private void insertImage(Bitmap image, String filePath) {
        Editable eb = etContent.getEditableText();
        //获得光标所在位置
        int qqPosition = etContent.getSelectionStart();
        String key = IMAGE_NAME + String.valueOf(mImageCount);
        //String key = String.valueOf(mImageCount++);
        SpannableString ss = new SpannableString(key);
        if (image.getWidth() > 500) {
            image = AppUtil.scaleImage(image, IMAGE_SIZE, AppUtil.getImageDegree(filePath), mContext);
        }

        //定义插入图片
        Drawable drawable = new BitmapDrawable(image);
        drawable.setBounds(2, 0, drawable.getIntrinsicWidth() + 2, drawable.getIntrinsicHeight() + 2);
        ss.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        eb.insert(qqPosition, ss);
        mImageHashMap.put(String.valueOf(mImageCount), new File(filePath));
        mImageCount++;
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
            Intent intent = new Intent();
            switch (mTypeCode) {
                case Const.REPLY:
                    //新增回复api
                    //Toast.makeText(this, "新增回复api", 500).show();
                    submitReply();

                case Const.EDIT_QUESTION:
                    //编辑问题api(未提供接口)
                    //Toast.makeText(this, "编辑问题api", 500).show();
                    break;
                case Const.EDIT_REPLY:
                    //编辑回复api
                    //Toast.makeText(this, "编辑回复api", 500).show();
                    submitReply();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 回复提交
     */
    private void submitReply() {
        //Log.e(TAG, Html.toHtml(etContent.getText()).toString());
        RequestUrl url = app.bindUrl(Const.REPLY_SUBMIT, true);
        HashMap<String, String> params = url.getParams();
        params.put("courseId", mCourseId);
        params.put("threadId", mThreadId);

//        params.put("content", etContent.getText().toString());
//        params.put("imageCount", "0");
//        File file = new File(filename);
//        Log.d(null, "file->" + file.length());
//        url.setMuiltParams(new Object[]{
//                "image1", file
//        });


        final String content = AppUtil.removeHtml(Html.toHtml(etContent.getText()));
        params.put("content", setContent(content));
        params.put("imageCount", String.valueOf(mImageHashMap.size()));
        url.setMuiltParams(mObjects);
        url.setParams(params);
        mProgressDialog.show();

        this.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    SubmitResult submitResult = mActivity.gson.fromJson(object, new TypeToken<SubmitResult>() {
                    }.getType());
                    mProgressDialog.cancel();
                    if (submitResult == null) {
                        return;
                    } else {
                        Toast.makeText(mContext, "提交成功", 500).show();
//                        Intent intent = new Intent();
//                        intent.putExtra(Const.REPLY_ID, mReplyId);
//                        intent.putExtra(Const.THREAD_ID, mThreadId);
//                        intent.putExtra(Const.COURSE_ID, mCourseId);
//                        intent.putExtra(Const.NORMAL_CONTENT, content);
//                        mActivity.setResult(Const.OK, intent);
                        mActivity.setResult(Const.OK);
                        mActivity.finish();
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

    /**
     * 获取图片的物理地址
     *
     * @param contentUri uri
     * @return
     */
    private String convertUriToPath(String contentUri) {
        Uri uri = Uri.parse(contentUri);
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    /**
     * 处理上传post内容，和上传图片信息
     *
     * @param strContent
     * @return
     */
    private String setContent(String strContent) {
        String str = etContent.getText().toString();
        int len = mImageHashMap.size();
        HashMap<String, Object> tmpHashMap = new HashMap<String, Object>();
        /**
         * 如果图片有删除，要把内存中对应图片名称删除
         */
        for (int i = 0; i < len; i++) {
            int tag = i + 1;
            if (!str.contains("image|" + tag)) {
                mImageHashMap.remove(String.valueOf(tag));
            }
        }

        if (mObjects == null) {
            mObjects = new Object[mImageHashMap.size() * 2];
        }

        Iterator iterator = mImageHashMap.entrySet().iterator();
        int objectFlags = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
            strContent = strContent.replaceFirst("<img src=\"null\">", "<img src=\"" + entry.getKey() + "\">");
            mObjects[objectFlags++] = entry.getKey();
            mObjects[objectFlags++] = entry.getValue();
        }
        return strContent;
    }

    public Html.ImageGetter imgGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            if (!source.contains("http")) {
                source = mHost + source;
            }
            Drawable drawable = null;
            try {
                Bitmap bitmap = mAQuery.getCachedImage(source);
                float showWidth = EdusohoApp.app.screenW * 0.8f;
                if (showWidth < bitmap.getHeight()) {
                    bitmap = AppUtil.scaleImage(bitmap, showWidth, 0, mContext);
                }
                drawable = new BitmapDrawable(bitmap);

                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            } catch (Exception ex) {
                Log.d("imageURL--->", ex.toString());
            }

            return drawable;
        }
    };

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(QuestionReplyActivity.this);
        mProgressDialog.setMessage("提交中...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
    }


    /**
     * 在编辑状态下，如果有图片把图片放入到mImageHashMap
     */
    private void setImageListByEditPost() {

    }

}
