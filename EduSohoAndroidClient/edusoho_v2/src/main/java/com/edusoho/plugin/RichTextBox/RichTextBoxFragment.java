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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hby on 14-10-16.
 */
public class RichTextBoxFragment extends Fragment implements View.OnClickListener {

    private ActionBarBaseActivity mActivity;
    private EdusohoApp app;

    public static final String HIT = "hit";
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
    private EditText etQuestionTitle;
    private EditText etTmp;
    private HorizontalScrollView mHSView;
    private LinearLayout mLinearImageList;
    private DisplayImageOptions mOptions;

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
    private String mTitle = null;
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
        initHorizontalScrollView();
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
        etQuestionTitle = (EditText) mRichTextBoxView.findViewById(R.id.et_title);
        mCourseId = mActivity.getIntent().getStringExtra(Const.COURSE_ID);
        mThreadId = mActivity.getIntent().getStringExtra(Const.THREAD_ID);
        mTypeCode = mActivity.getIntent().getIntExtra(Const.REQUEST_CODE, 0);
        mHSView = (HorizontalScrollView) mRichTextBoxView.findViewById(R.id.hs_image_list);
        mLinearImageList = (LinearLayout) mRichTextBoxView.findViewById(R.id.ll_horizontal_image_list);
        mHSView.setHorizontalScrollBarEnabled(false);
        mHSView.setVerticalScrollBarEnabled(false);
        etTmp = new EditText(mContext);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();

        Bundle bundle = getArguments();
        etContent.setHint(bundle.containsKey(HIT) ? bundle.getString(HIT) : "添加内容");
        if (mTypeCode == Const.EDIT_QUESTION) {
            mOriginalContent = mActivity.getIntent().getStringExtra(Const.QUESTION_CONTENT);
            mTitle = mActivity.getIntent().getStringExtra(Const.QUESTION_TITLE);
            Html.fromHtml(AppUtil.filterSpace(mOriginalContent), mImageGetter, new EduTagHandler());
            etContent.setText(Html.fromHtml(removeImgTagFromString(mOriginalContent), null, new EduTagHandler()));
        } else if (mTypeCode == Const.EDIT_REPLY) {
            mPostId = mActivity.getIntent().getStringExtra(Const.POST_ID);
            mOriginalContent = mActivity.getIntent().getStringExtra(Const.NORMAL_CONTENT);
            Html.fromHtml(AppUtil.filterSpace(mOriginalContent), mImageGetter, new EduTagHandler());
            etContent.setText(Html.fromHtml(removeImgTagFromString(mOriginalContent), null, new EduTagHandler()));
        } else if (mTypeCode == Const.REPLY) {
            mPostId = "";
        } else {
            mOriginalContent = mActivity.getIntent().getStringExtra(Const.NORMAL_CONTENT);
            Html.fromHtml(AppUtil.filterSpace(mOriginalContent), mImageGetter, new EduTagHandler());
            etContent.setText(Html.fromHtml(removeImgTagFromString(mOriginalContent), null, new EduTagHandler()));
        }

        //初始化字体颜色选择画板
        if (mColorPickerDialog == null) {
            mColorPickerDialog = new ColorPickerDialog(mActivity, Color.BLACK);
            mColorPickerDialog.setOnColorChangedListener(mOnColorChangedListener);
        }

        if (mTitle != null) {
            etQuestionTitle.setVisibility(View.VISIBLE);
            etQuestionTitle.setText(mTitle);
        } else {
            etQuestionTitle.setVisibility(View.GONE);
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

        etContent.setSelection(etContent.getText().length());

        //mCurFontSize = (int) etContent.getTextSize();
    }

    private Html.ImageGetter mImageGetter = new Html.ImageGetter() {

        @Override
        public Drawable getDrawable(String source) {
            if (!source.contains("http")) {
                source = EdusohoApp.app.host + source;
            }
            try {
                final String finalSource = source;
                ImageLoader.getInstance().loadImage(source, mOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        insertImageIntoHorizontalList(bitmap);
                        mImageCount++;
                        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //AppUtil.compressImage(bitmap, baos, 0);
                        //mImageHashMap.put(String.valueOf(mImageCount), AppUtil.createFile(AQUtility.getCacheDir(mContext).getPath(), baos, mCompressImageName++));
                        //mImageHashMap.put(String.valueOf(mImageCount), new File(ImageLoader.getInstance().getDiskCache().get(finalSource).getPath()));
                        //mImageCount++;
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        //暂时先考虑多图同路径就触发这个方法，可能也有其他情况。
                        Bitmap bitmap = BitmapFactory.decodeFile(ImageLoader.getInstance().getDiskCache().get(finalSource).getPath());
                        insertImageIntoHorizontalList(bitmap);
                    }
                });
            } catch (Exception ex) {
                Log.d("imageURL--->", ex.toString());
            }
            return new BitmapDrawable();
        }
    };

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

    private void initHorizontalScrollView() {
        HorizontalScrollView hsView = (HorizontalScrollView) mRichTextBoxView.findViewById(R.id.hs_image_list);
        ViewGroup.LayoutParams lp = hsView.getLayoutParams();
        lp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        lp.height = (int) (EdusohoApp.screenW * 0.1f);
        hsView.setLayoutParams(lp);
        hsView.setVisibility(View.GONE);
    }

    /**
     * 插入图片到HorizontalScrollView
     *
     * @param image
     */
    private void insertImageIntoHorizontalList(Bitmap image) {
        try {
            ViewGroup.LayoutParams lp = mHSView.getLayoutParams();
            lp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            lp.height = (int) (EdusohoApp.screenW * 0.21f);
            mHSView.setLayoutParams(lp);
            mHSView.setVisibility(View.VISIBLE);

            //每个图片是个RelativeLayout
            RelativeLayout relativeLayout1 = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams rlp1 = new RelativeLayout.LayoutParams((int) (EdusohoApp.screenW * 0.21f),
                    (int) (EdusohoApp.screenW * 0.21f));
            int marginSpace = (int) (EdusohoApp.screenW * 0.01f);
            relativeLayout1.setPadding(marginSpace, marginSpace, marginSpace, marginSpace);
            mLinearImageList.addView(relativeLayout1, rlp1);

            //RelativeLayout中的图片
            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            RelativeLayout.LayoutParams ivLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            relativeLayout1.addView(imageView, ivLayoutParams);

            //RelativeLayout中的删除按钮
            ImageView ivDel = new ImageView(mContext);
            ivDel.setBackgroundColor(Color.parseColor("#20000000"));
            ivDel.setImageDrawable(getResources().getDrawable(R.drawable.iconfont_image_del));
            RelativeLayout.LayoutParams tvDelLayoutParams = new RelativeLayout.LayoutParams((int) (EdusohoApp.screenW * 0.2f * 0.2),
                    (int) (EdusohoApp.screenW * 0.2f * 0.2));
            ivDel.setPadding(2, 2, 2, 2);
            tvDelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            tvDelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            relativeLayout1.addView(ivDel, tvDelLayoutParams);
            ivDel.setTag(mImageCount);
            ivDel.setOnClickListener(mImageDelClick);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    /**
     * 图片删除事件
     */
    private View.OnClickListener mImageDelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                int imageIndex = Integer.valueOf(v.getTag().toString());
                mImageHashMap.remove(String.valueOf(imageIndex));
                RelativeLayout parent = (RelativeLayout) v.getParent();
                int viewIndex = ((ViewGroup) parent.getParent()).indexOfChild(parent);
                mLinearImageList.removeViewAt(viewIndex);
                if (mLinearImageList.getChildCount() == 0) {
                    mHSView.setVisibility(View.GONE);
                }
                etTmp.setText(Html.fromHtml(AppUtil.filterSpace(removeImgTag(Html.toHtml(etTmp.getText()), viewIndex))));

//                etContent.setText(Html.fromHtml(addSplitImgTag(AppUtil.filterSpace(strTmp)), new Html.ImageGetter() {
//                    @Override
//                    public Drawable getDrawable(String source) {
//                        return new BitmapDrawable();
//                    }
//                }, new EduTagHandler()));
//                mImageCount--;

            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }
        }
    };

    /**
     * 光标处插入图片
     *
     * @param image
     */
    private void insertImage(Bitmap image, String filePath) {
        try {
            //etContent.getText().insert(etContent.getSelectionEnd(), "\n");
            //Editable eb = etContent.getEditableText();
            //获得光标所在位置
            int qqPosition = etContent.getSelectionStart();
            String key = IMAGE_NAME + String.valueOf(mImageCount);
            //String key = String.valueOf(mImageCount++);
            SpannableString ss = new SpannableString(key);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (image.getWidth() > (EdusohoApp.app.screenW * 0.8f)) {
                image = AppUtil.scaleImage(image, EdusohoApp.app.screenW * 0.8f, AppUtil.getImageDegree(filePath), mContext);
            }
            if (AppUtil.getImageSize(image) > IMAGE_SIZE) {
                image = AppUtil.compressImage(image, baos, 50);
            } else {
                image = AppUtil.compressImage(image, baos, 100);
            }

            //插入图片
            Drawable drawable = new BitmapDrawable(image);
            //int start = (etContent.getWidth() - drawable.getIntrinsicWidth()) / 2;
//        drawable.setBounds(start, 2, drawable.getIntrinsicWidth() + start, drawable.getIntrinsicHeight() + 2);
//        ss.setSpan(new ImageSpan(drawable), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            drawable.setBounds(0, 0, 0, 0);
            ss.setSpan(new ImageSpan(drawable), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            etTmp.getText().insert(etTmp.getSelectionStart(), ss);
            //etContent.getText().insert(etContent.getSelectionEnd(), "\n");
            insertImageIntoHorizontalList(image);
            //保存的是压缩后的图片
            mImageHashMap.put(String.valueOf(mImageCount), AppUtil.createFile(AQUtility.getCacheDir(mContext).getPath(), baos, mCompressImageName++));
            mImageCount++;
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    private ColorPickerDialog.OnColorChangedListener mOnColorChangedListener = new ColorPickerDialog.OnColorChangedListener() {
        @Override
        public void onColorChanged(int color) {
            ForegroundColorSpan fcs = new ForegroundColorSpan(color);
            etContent.getText().removeSpan(getForeColorStyle());
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
        try {
//            String str = etContent.getText().toString();
//            int len = mImageHashMap.size();
//            HashMap<String, Object> tmpHashMap = new HashMap<String, Object>();
            /**
             * 如果图片有删除，要把HashMap中对应图片名称删除
             */
//            for (int i = 0; i < len; i++) {
//                int tag = i + 1;
//                if (!str.contains("image|" + tag)) {
//                    mImageHashMap.remove(String.valueOf(tag));
//                }
//            }

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
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
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
                if (bitmap == null) {
                    ImageLoader.getInstance().loadImage(source, new DisplayImageOptions.Builder().cacheOnDisk(true).build(),
                            new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String imageUri, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    //bitmap = loadedImage;
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri, View view) {

                                }
                            });
                } else {
                    float showWidth = EdusohoApp.app.screenW * 0.8f;
                    if (showWidth < bitmap.getHeight()) {
                        bitmap = AppUtil.scaleImage(bitmap, showWidth, 0, mContext);
                    }
                    drawable = new BitmapDrawable(bitmap);

                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                }
            } catch (Exception ex) {
                Log.d("imageURL--->", ex.toString());
            }

            return drawable;
        }
    };


    public Editable getContent() {
        return etContent.getText();
    }

    public Editable getImageContent() {
        return etTmp.getText();
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

    public TextView getTitle() {
        return etQuestionTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
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
        mCurrentStyle = etContent.getText().getSpans(etContent.getSelectionStart(), etContent.getSelectionEnd(),
                CharacterStyle.class);
        for (int i = 0; i < mCurrentStyle.length; i++) {
            if (mCurrentStyle[i] instanceof AbsoluteSizeSpan) {
                return (AbsoluteSizeSpan) mCurrentStyle[i];
            }
        }
        return null;
    }

    private ForegroundColorSpan getForeColorStyle() {
        mCurrentStyle = etContent.getText().getSpans(etContent.getSelectionStart(), etContent.getSelectionEnd(),
                CharacterStyle.class);
        for (int i = 0; i < mCurrentStyle.length; i++) {
            if (mCurrentStyle[i] instanceof ForegroundColorSpan) {
                return (ForegroundColorSpan) mCurrentStyle[i];
            }
        }
        return null;
    }

    private String addSplitImgTag(String content) {
        Matcher m = Pattern.compile("(<img src=\".*?\" .>)").matcher(content);
        while (m.find()) {
            content = content.replace(m.group(1), "<p>" + m.group(1) + "</p>");
        }
        return content;
    }

    /**
     * 去掉所有<Img>标签
     *
     * @param content
     * @return
     */
    private String removeImgTagFromString(String content) {
        Matcher m = Pattern.compile("(<img src=\".*?\" .>)").matcher(content);
        while (m.find()) {
            content = content.replace(m.group(1), "");
            etTmp.setText(Html.fromHtml(Html.toHtml(etTmp.getText()) + m.group(1)));
        }
        return content;
    }

    /**
     * 根据index删除文本中的<img>标签
     *
     * @param content
     * @param index
     * @return
     */
    private String removeImgTag(String content, int index) {
        Matcher m = Pattern.compile("(<img src=\".*?\">)").matcher(content);
        StringBuffer stringBuffer = new StringBuffer();
        int tag = 0;
        while (m.find()) {
            if (tag == index) {
                m.appendReplacement(stringBuffer, "");
                break;
            }
            tag++;
        }
        m.appendTail(stringBuffer);
        Log.d("null-->", stringBuffer.toString());
        return stringBuffer.toString();
    }

}
