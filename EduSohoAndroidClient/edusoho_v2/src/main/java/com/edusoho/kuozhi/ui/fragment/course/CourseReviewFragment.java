package com.edusoho.kuozhi.ui.fragment.course;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseTeacherAdapter;
import com.edusoho.kuozhi.adapter.ReviewListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.ReviewResult;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseReviewFragment extends BaseFragment {

    private RefreshListWidget mListView;

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_review_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mListView = (RefreshListWidget) view.findViewById(R.id.list_view);

        ReviewListAdapter mAdapter = new ReviewListAdapter(
                mContext, R.layout.course_details_review_item);
        mListView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        int mCourseId = bundle.getInt(Const.COURSE_ID, 0);
        getReviews(0, mCourseId);
    }

    public void getReviews(int start, int courseId)
    {
        RequestUrl url = mActivity.app.bindUrl(Const.REVIEWS, true);
        url.setParams(new String[] {
                Const.COURSE_ID,  String.valueOf(courseId),
                "start", String.valueOf(start),
                "limit", String.valueOf(Const.LIMIT)
        });

        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mListView.onRefreshComplete();
                ReviewResult reviewResult = mActivity.parseJsonValue(
                        object, new TypeToken<ReviewResult>(){});

                if (reviewResult == null || reviewResult.total == 0) {
                    mListView.pushData(null);
                    return;
                }

                mListView.pushData(reviewResult.data);
                mListView.setStart(reviewResult.start, reviewResult.total);
            }
        });
    }
}
