package com.edusoho.kuozhi.ui.fragment.course;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseLessonAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.entity.LessonsResult;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseLessonsFragment extends BaseFragment {

    private EduSohoListView mListView;
    private int mCourseId;
    private CourseLessonAdapter mAdapter;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_lesson_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mListView = (EduSohoListView) view.findViewById(R.id.list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mAdapter = new CourseLessonAdapter(
                mActivity, R.layout.course_details_learning_lesson_item);
        mListView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        }
        loadLessons(false);
    }

    private void loadLessons(boolean mIsAddToken)
    {
        RequestUrl url = mActivity.app.bindUrl(Const.LESSONS, mIsAddToken);
        url.setParams(new String[]{
                Const.COURSE_ID, String.valueOf(mCourseId)
        });
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LessonsResult lessonsResult = mActivity.parseJsonValue(
                        object, new TypeToken<LessonsResult>(){});
                if (lessonsResult == null) {
                    return;
                }

                mListView.pushData(lessonsResult.lessons);
                mAdapter.updateLearnStatus(lessonsResult.learnStatuses);
            }
        });
    }
}
