package com.edusoho.kuozhi.ui.classRoom;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.WeekCourseAdapter;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.ui.course.CoursePaperActivity;
import com.edusoho.kuozhi.ui.fragment.course.ViewPagerBaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.DividerItemDecoration;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by howzhi on 15/7/13.
 */
public class ClassRoomCourseFragment extends ViewPagerBaseFragment {

    private int mClassRoomId;
    private EduSohoListView mListView;
    private WeekCourseAdapter mAdapter;

    @Override
    public EduSohoListView getListView() {
        return mListView;
    }

    @Override
    public String getTitle() {
        return "课程";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.classroom_course_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mListView = (EduSohoListView) view.findViewById(R.id.list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));
        mListView.setEmptyString(new String[]{"暂无课程"}, R.drawable.icon_course_empty);
        mAdapter = new WeekCourseAdapter(
                mContext, R.layout.found_course_list_item);
        mListView.setAdapter(mAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mClassRoomId = bundle.getInt(Const.CLASSROOM_ID, 0);
        }

        loadClassRoomCourses();
    }

    private void loadClassRoomCourses() {
        RequestUrl url = app.bindUrl(Const.CLASSROOM_COURSES, false);
        url.setParams(new String[]{
                Const.CLASSROOM_ID, String.valueOf(mClassRoomId)
        });
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                ArrayList<Course> courses = mActivity.parseJsonValue(
                        object, new TypeToken<ArrayList<Course>>() {
                        });
                mListView.pushData(courses);
            }
        });
        mAdapter.setOnItemClick(new RecyclerViewListBaseAdapter.RecyclerItemClick() {
            @Override
            public void onItemClick(Object obj, int position) {
                Course course = (Course)obj;
                Bundle bundle = new Bundle();
                bundle.putInt(Const.COURSE_ID, course.id);
                bundle.putInt(CoursePaperActivity.FROM_TARGET, CoursePaperActivity.FROM_CLASSROOM);
                bundle.putString(Const.ACTIONBAR_TITLE, course.title);
                mActivity.app.mEngine.runNormalPluginWithBundle("CoursePaperActivity", mActivity, bundle);
            }
        });
    }
}
