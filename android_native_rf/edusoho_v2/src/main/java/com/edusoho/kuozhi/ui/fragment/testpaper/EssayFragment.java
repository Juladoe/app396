package com.edusoho.kuozhi.ui.fragment.testpaper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.testpaper.EssayViewPagerAdapter;
import com.edusoho.kuozhi.adapter.testpaper.QuestionAdapter;
import com.edusoho.kuozhi.model.Testpaper.QuestionType;
import com.edusoho.kuozhi.model.Testpaper.QuestionTypeSeq;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-24.
 */
public class EssayFragment extends SelectQuestionFragment{

    private QuestionType type = QuestionType.essay;
    private static final int IMAGE_SIZE = 500;
    private EditText mContentView;

    /**
     * 图片数量
     */
    private int mImageCount = 1;

    @Override
    public String getTitle() {
        return "问答题";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.choice_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        refreshViewData();
    }

    @Override
    protected void refreshViewData() {
        ArrayList<QuestionTypeSeq> questionTypeSeqs = getQuestion(type);
        if (questionTypeSeqs == null) {
            return;
        }
        mQuestionType.setText(type.title());
        mQuestionCount = questionTypeSeqs.size();
        setQuestionNumber(mCurrentIndex);

        QuestionAdapter adapter = new QuestionAdapter(
                mContext, questionTypeSeqs);
        mQuestionPager.setAdapter(adapter);
    }

    /**
     * 光标处插入图片
     *
     * @param imageName
     * @param image
     */
    private void insertImage(String imageName, Bitmap image) {
        Editable eb = mContentView.getEditableText();
        //获得光标所在位置
        int qqPosition = mContentView.getSelectionStart();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
