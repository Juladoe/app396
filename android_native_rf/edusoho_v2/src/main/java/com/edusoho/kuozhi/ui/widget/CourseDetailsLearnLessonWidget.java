package com.edusoho.kuozhi.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LearnLessonListAdapter;
import com.edusoho.kuozhi.adapter.LessonListAdapter;
import com.edusoho.kuozhi.model.LessonItem;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-9.
 */
public class CourseDetailsLearnLessonWidget extends CourseDetailsLessonWidget {

    public CourseDetailsLearnLessonWidget(Context context) {
        super(context);
    }

    public CourseDetailsLearnLessonWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void setAdapter(ArrayList<LessonItem> lessonItems) {
        LearnLessonListAdapter adapter = new LearnLessonListAdapter(
                mContext, lessonItems, null, mResourceRes);
        mContentView.setAdapter(adapter);
    }

    @Override
    protected void initView(AttributeSet attrs) {
        setResource(R.layout.course_details_learning_lesson_item);
        super.initView(attrs);
    }
}
