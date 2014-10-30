package com.edusoho.plugin.RichTextBox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduTagHandler;
import com.edusoho.plugin.FontColorPicker.ColorPickerDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by hby on 14-10-16.
 */
public class RichTextBoxFragment extends Fragment implements View.OnClickListener {

    private ActionBarBaseActivity mActivity;
    private EdusohoApp app;

    private static final String TAG = "RichTextBoxFragment";
    private static final int IMAGE_WIDTH = 500;
    private static final int IMAGE_SIZE = 1024 * 500;


    private ImageView ivBoldStyle;
    private ImageView ivItalicStyle;
    private ImageView ivUnderLineStyle;
    private ImageView ivFontColor;
    private ImageView ivFontSizeIncre;
    private ImageView ivFontSizeDecre;
    private ImageView ivCamera;
    private ImageView ivPhoto;

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

    //private int mCurFontSize = 0;
    private int mMaxFontSize = 70;
    private int mMinFontSize = 20;

    private Object[] mObjects;

    private EditText etContent;

    private String mCourseId;
    private String mThreadId;

    /**
     * id
     */
    private String mPostId;
    private String mTitle;
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

    private int mTypeCode;
    private AQuery mAQuery;
    private Context mContext;
    private File mCameraImageFile;

    private ProgressDialog mProgressDialog;
    private ColorPickerDialog mColorPickerDialog;

    private View mRichTextBoxView;

    private byte[] mItemArgs = new byte[7];
    private int mCompressImageName = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRichTextBoxView = inflater.inflate(R.layout.richtextbox_layout, container, false);
        initViews();
        initProgressDialog();
        Bundle bundle = getArguments();
        if (bundle != null) {
            mItemArgs = bundle.getByteArray(Const.RICH_ITEM_AGRS);
            setItemVisible();
        }
        return mRichTextBoxView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarBaseActivity) activity;
        mContext = mActivity.getBaseContext();
        app = mActivity.app;
    }

    private void initViews() {
        mAQuery = new AQuery(mRichTextBoxView);
        etContent = (EditText) mRichTextBoxView.findViewById(R.id.et_content);
        ivBoldStyle = (ImageView) mRichTextBoxView.findViewById(R.id.iv_bold);
        ivItalicStyle = (ImageView) mRichTextBoxView.findViewById(R.id.iv_italic);
        ivUnderLineStyle = (ImageView) mRichTextBoxView.findViewById(R.id.iv_underline);
        ivFontColor = (ImageView) mRichTextBoxView.findViewById(R.id.iv_font_color);
        ivFontSizeIncre = (ImageView) mRichTextBoxView.findViewById(R.id.iv_font_increase);
        ivFontSizeDecre = (ImageView) mRichTextBoxView.findViewById(R.id.iv_font_decrease);
        ivPhoto = (ImageView) mRichTextBoxView.findViewById(R.id.iv_photo);
        ivCamera = (ImageView) mRichTextBoxView.findViewById(R.id.iv_camera);

        mCourseId = mActivity.getIntent().getStringExtra(Const.COURSE_ID);
        mThreadId = mActivity.getIntent().getStringExtra(Const.THREAD_ID);
        mTypeCode = mActivity.getIntent().getIntExtra(Const.REQUEST_CODE, 0);

        if (mTypeCode == Const.EDIT_QUESTION) {
            mOriginalContent = mActivity.getIntent().getStringExtra(Const.QUESTION_CONTENT);
            mTitle = mActivity.getIntent().getStringExtra(Const.QUESTION_TITLE);
            etContent.setText(AppUtil.setHtmlContent(Html.fromHtml(mOriginalContent, imgGetter, new EduTagHandler())));
        } else if (mTypeCode == Const.EDIT_REPLY) {
            mPostId = mActivity.getIntent().getStringExtra(Const.POST_ID);
            mOriginalContent = mActivity.getIntent().getStringExtra(Const.NORMAL_CONTENT);
            etContent.setText(AppUtil.setHtmlContent(Html.fromHtml(mOriginalContent, imgGetter, new EduTagHandler())));
        } else if (mTypeCode == Const.REPLY) {
            mPostId = "";
        } else {
            mOriginalContent = mActivity.getIntent().getStringExtra(Const.NORMAL_CONTENT);
            etContent.setText(AppUtil.setHtmlContent(Html.fromHtml(mOriginalContent, imgGetter, new EduTagHandler())));
        }

        if (mColorPickerDialog == null) {
            mColorPickerDialog = new ColorPickerDialog(mActivity, Color.BLACK);
            mColorPickerDialog.setOnColorChangedListener(mOnColorChangedListener);
        }

        etContent.setOnClickListener(this);
        ivBoldStyle.setOnClickListener(richTextListener);
        ivItalicStyle.setOnClickListener(richTextListener);
        ivUnderLineStyle.setOnClickListener(richTextListener);
        ivFontColor.setOnClickListener(richTextListener);
        ivFontSizeIncre.setOnClickListener(richTextListener);
        ivFontSizeDecre.setOnClickListener(richTextListener);
        ivPhoto.setOnClickListener(richTextListener);
        ivCamera.setOnClickListener(richTextListener);

        //mCurFontSize = (int) etContent.getTextSize();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.et_content) {
            mCurrentStyle = etContent.getText().getSpans(etContent.getSelectionStart(), etContent.getSelectionEnd(),
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
                //粗体设置
                if (ivBoldStyle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon_font_bold).getConstantState())) {
                    ivBoldStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold_on_click));
                    etContent.getText().setSpan(mStyleBold, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                } else {
                    ivBoldStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold));
                    etContent.getText().removeSpan(mStyleBold);
                }
            } else if (v.getId() == R.id.iv_italic) {
                //斜体设置
                if (ivItalicStyle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon_font_italic).getConstantState())) {
                    ivItalicStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_italic_on_click));
                    etContent.getText().setSpan(mStyleItalic, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                } else {
                    ivItalicStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_italic));
                    etContent.getText().removeSpan(mStyleItalic);
                }
            } else if (v.getId() == R.id.iv_underline) {
                //下划线设置
                if (ivUnderLineStyle.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.icon_font_under_line).getConstantState())) {
                    ivUnderLineStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_under_line_on_click));
                    etContent.getText().setSpan(mStyleUnderline, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                } else {
                    ivUnderLineStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_under_line));
                    etContent.getText().removeSpan(mStyleUnderline);
                }
            } else if (v.getId() == R.id.iv_font_color) {
                mColorPickerDialog.show();
            } else if (v.getId() == R.id.iv_font_increase) {
                //字体增大
                int curFontSize = getSelectTextSize();
                if (curFontSize == 0) {
                    curFontSize = (int) etContent.getTextSize();
                }
                if (mSelectTextEnd - mSelectTextStart > 0) {
                    if (curFontSize < mMaxFontSize) {
                        etContent.getText().removeSpan(getFontSizeStyle());
                        ivFontSizeIncre.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_size_increase));
                        ivFontSizeDecre.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_size_decrease));
                        curFontSize = curFontSize + 5;
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(curFontSize);
                        etContent.getText().setSpan(ass, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    } else {
                        ivFontSizeIncre.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_size_increase_unclick));
                    }
                }
            } else if (v.getId() == R.id.iv_font_decrease) {
                //字体减小
                int curFontSize = getSelectTextSize();
                if (curFontSize == 0) {
                    curFontSize = (int) etContent.getTextSize();
                }
                if (mSelectTextEnd - mSelectTextStart > 0) {
                    if (curFontSize > mMinFontSize) {
                        etContent.getText().removeSpan(getFontSizeStyle());
                        ivFontSizeIncre.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_size_increase));
                        ivFontSizeDecre.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_size_decrease));
                        curFontSize = curFontSize - 5;
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(curFontSize);
                        etContent.getText().setSpan(ass, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    } else {
                        ivFontSizeDecre.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_size_decrease_unclick));
                    }
                }
            } else if (v.getId() == R.id.iv_photo) {
                //相册图片
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_RESULT);
            } else if (v.getId() == R.id.iv_camera) {
                //拍照
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    //String saveDir = Environment.getExternalStorageDirectory().getPath() + "/temp_image";
                    String saveDir = ImageLoader.getInstance().getDiskCache().getDirectory().getAbsolutePath();
                    mCameraImageFile = new File(saveDir, "CameraImage_" + mCameraIndex + ".jpg");
                    mCameraIndex++;
                    if (!mCameraImageFile.exists()) {
                        try {
                            mCameraImageFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            mCameraIndex--;
                            Toast.makeText(mContext, "照片创建失败!", Toast.LENGTH_LONG).show();
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

    private int getSelectTextSize() {
        CharacterStyle[] styles = etContent.getText().getSpans(etContent.getSelectionStart(), etContent.getSelectionEnd(),
                CharacterStyle.class);
        for (CharacterStyle style : styles) {
            if (style instanceof AbsoluteSizeSpan) {
                return ((AbsoluteSizeSpan) style).getSize();
            }
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_RESULT:
                if (null != data) {
                    //根据需要，也可以加上Option这个参数
                    InputStream inputStream = null;
                    try {
                        Uri uri = data.getData();
                        inputStream = mActivity.getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    String filePath = convertUriToPath(data.getDataString());

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    insertImage(bitmap, filePath);
                }
                break;
            case CAMERA_RESULT:
                if (resultCode == mActivity.RESULT_OK) {
                    if (mCameraImageFile != null & mCameraImageFile.exists()) {
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(mCameraImageFile.getPath(), option);
                        insertImage(bitmap, mCameraImageFile.getPath());
                        mCameraImageFile.delete();
                    }
                }
                break;
        }
    }

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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (image.getWidth() > 500) {
            image = AppUtil.scaleImage(image, IMAGE_WIDTH, AppUtil.getImageDegree(filePath), mContext);
        }
        if (AppUtil.getImageSize(image) > IMAGE_SIZE) {
            image = AppUtil.compressImage(image, baos, 50);
        } else {
            image = AppUtil.compressImage(image, baos, 100);
        }

        //插入图片
        Drawable drawable = new BitmapDrawable(image);
        drawable.setBounds(2, 0, drawable.getIntrinsicWidth() + 2, drawable.getIntrinsicHeight() + 2);
        ss.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        eb.insert(qqPosition, ss);

        //保存的是压缩后的图片
        mImageHashMap.put(String.valueOf(mImageCount), AppUtil.createFile(AQUtility.getCacheDir(mContext).getPath(), baos, mCompressImageName++));
        mImageCount++;
    }

    private ColorPickerDialog.OnColorChangedListener mOnColorChangedListener = new ColorPickerDialog.OnColorChangedListener() {
        @Override
        public void onColorChanged(int color) {
            ForegroundColorSpan fcs = new ForegroundColorSpan(color);
            etContent.getText().setSpan(fcs, mSelectTextStart, mSelectTextEnd, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
    };

    private void setRichTextImage() {
        ivBoldStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_bold));
        ivItalicStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_italic));
        ivUnderLineStyle.setImageDrawable(getResources().getDrawable(R.drawable.icon_font_under_line));

        //多选文字，不自动变style
        if ((etContent.getSelectionEnd() - etContent.getSelectionStart()) == 0) {
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
    }

    /**
     * 获取图片的物理地址
     *
     * @param contentUri uri
     * @return
     */
    private String convertUriToPath(String contentUri) {
        Uri uri = Uri.parse(contentUri);
        ContentResolver cr = mActivity.getContentResolver();
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
    public String setContent(String strContent) {
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

    /**
     * 初始化对话框
     */
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("提交中...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
    }

    public Html.ImageGetter imgGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            if (!source.contains("http")) {
                source = app.host + source;
            }
            Drawable drawable = null;
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiskCache().get(source).getPath());
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


    public Editable getContent() {
        return etContent.getText();
    }

    public int getTypeCode() {
        return this.mTypeCode;
    }

    public String getCourseId() {
        return mCourseId;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public String getPostId() {
        return mPostId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getImageHashMapSize() {
        return mImageHashMap.size();
    }

    public Object[] getObjects() {
        return mObjects;
    }

    /**
     * 控制显示富文本编辑框字体设置，参数：View.VISIBLE,View.GONE
     */
    private void setItemVisible() {
        if (mItemArgs.length > 1) {
            ivBoldStyle.setVisibility(mItemArgs[0]);
            ivItalicStyle.setVisibility(mItemArgs[1]);
            ivUnderLineStyle.setVisibility(mItemArgs[2]);
            ivFontColor.setVisibility(mItemArgs[3]);
            ivFontSizeIncre.setVisibility(mItemArgs[4]);
            ivFontSizeDecre.setVisibility(mItemArgs[4]);
            ivCamera.setVisibility(mItemArgs[5]);
            ivPhoto.setVisibility(mItemArgs[6]);
        }
    }

    private AbsoluteSizeSpan getFontSizeStyle() {
        mCurrentStyle = etContent.getText().getSpans(etContent.getSelectionStart() - 1, etContent.getSelectionEnd(),
                CharacterStyle.class);
        for (int i = 0; i < mCurrentStyle.length; i++) {
            if (mCurrentStyle[i] instanceof AbsoluteSizeSpan) {
                return (AbsoluteSizeSpan) mCurrentStyle[i];
            }
        }
        return null;
    }

}
