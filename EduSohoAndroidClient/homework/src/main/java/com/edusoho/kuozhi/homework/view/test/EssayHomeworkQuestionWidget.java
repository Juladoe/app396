package com.edusoho.kuozhi.homework.view.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.homework.HomeworkActivity;
import com.edusoho.kuozhi.homework.R;
import com.edusoho.kuozhi.homework.adapter.EssayImageSelectAdapter;
import com.edusoho.kuozhi.homework.model.HomeWorkQuestion;
import com.edusoho.kuozhi.homework.ui.fragment.HomeWorkQuestionFragment;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.test.TestpaperActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-29.
 */
public class EssayHomeworkQuestionWidget extends BaseHomeworkQuestionWidget {

    private EditText contentEdt;
    private ArrayList<String> mRealImageList;
    private View mToolsLayout;
    private GridView mImageGridView;
    private EssayImageSelectAdapter mImageGridViewAdapter;

    private static final int IMAGE_SIZE = 500;

    public static final int GET_PHOTO = 0001;
    public static final int GET_CAMERA = 0002;

    /**
     * 图片数量
     */
    private int mImageCount = 1;

    public EssayHomeworkQuestionWidget(Context context) {
        super(context);
    }

    public EssayHomeworkQuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mImageGridViewAdapter.getCount() >= 7) {
                CommonUtil.longToast(mContext, "上传图片仅限5张!");
                return;
            }
            EssayImageSelectAdapter.GridViewItem item = (EssayImageSelectAdapter.GridViewItem)
                    parent.getItemAtPosition(position);
            switch (item.type) {
                case EssayImageSelectAdapter.SEL_IMG:
                    MessageEngine.getInstance().sendMsgToTagetForCallback(
                            HomeWorkQuestionFragment.PHOTO, null, HomeWorkQuestionFragment.class, new NormalCallback() {
                                @Override
                                public void success(Object obj) {
                                    addImageToGridView((Bundle) obj);
                                }
                            });
                    break;
                case EssayImageSelectAdapter.CAMERA_IMG:
                    MessageEngine.getInstance().sendMsgToTagetForCallback(
                            HomeWorkQuestionFragment.CAMERA, null, HomeWorkQuestionFragment.class, new NormalCallback() {
                                @Override
                                public void success(Object obj) {
                                    addImageToGridView((Bundle) obj);
                                }
                            });
                    break;
                case EssayImageSelectAdapter.SHOW_IMG:


            }

        }
    };

    private void addImageToGridView(Bundle bundle) {
        String filePath = bundle.getString("file");
        String realImagePath = bundle.getString("image");
        Bitmap bitmap = AppUtil.getBitmapFromFile(new File(filePath), AppUtil.dp2px(mContext, 40));
        mRealImageList.add(realImagePath);
        mImageGridViewAdapter.insertItem(bitmap);
        updateAnswerData(getRealImageStr().toString());
    }

    private TextWatcher onTextChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            updateAnswerData(getRealImageStr().append(charSequence).toString());

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    public StringBuilder getRealImageStr() {
        StringBuilder builder = new StringBuilder();
        for (String image : mRealImageList) {
            builder.append(String.format("<img src='%s' />", image));
        }

        return builder;
    }

    private void updateAnswerData(String answerStr) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", mIndex - 1);
        bundle.putString("QuestionType", QuestionType.material.name());
        ArrayList<String> data = new ArrayList<String>();
        data.add(answerStr);
        bundle.putStringArrayList("data", data);
        MessageEngine.getInstance().sendMsgToTaget(
                HomeworkActivity.CHANGE_ANSWER, bundle, HomeworkActivity.class);
    }

    @Override
    protected void invalidateData() {
        super.invalidateData();

        mToolsLayout = this.findViewById(R.id.hw_essay_tools_layout);
        contentEdt = (EditText) this.findViewById(R.id.hw_essay_content);
        mImageGridView = (GridView) findViewById(R.id.hw_essay_select_gridview);
        contentEdt.addTextChangedListener(onTextChangedListener);

        mImageGridViewAdapter = new EssayImageSelectAdapter(mContext);
        mImageGridView.setAdapter(mImageGridViewAdapter);
        mImageGridView.setOnItemClickListener(mClickListener);
        mRealImageList = new ArrayList<>(5);
    }

    private class NetImageGetter implements Html.ImageGetter {
        private TextView mTextView;
        private String html;

        public NetImageGetter(TextView textView, String html) {
            this.html = html;
            mTextView = textView;
        }

        @Override
        public Drawable getDrawable(String s) {
            if (TextUtils.isEmpty(s)) {
                return getResources().getDrawable(R.drawable.html_image_fail);
            }
            Drawable drawable = getResources().getDrawable(R.drawable.load);
            File file = ImageLoader.getInstance().getDiskCache().get(s);

            if (file != null && file.exists()) {
                Bitmap bitmap = AppUtil.getBitmapFromFile(file);
                drawable = new BitmapDrawable(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }

            ImageLoader.getInstance().loadImage(s, EdusohoApp.app.mOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    mTextView.setText(Html.fromHtml(html, new NetImageGetter(mTextView, html), null));
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });

            return drawable;
        }
    }

    @Override
    protected void initView(AttributeSet attrs) {
    }

    @Override
    public void setData(HomeWorkQuestion questionSeq, int index) {
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
        int bounding = AppUtil.dp2px(mContext, IMAGE_SIZE);

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
