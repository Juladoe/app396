package com.edusoho.kuozhi.ui.fragment.course;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseIntroductionAdapter;
import com.edusoho.kuozhi.adapter.Course.CourseTeacherAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseTeacherInfoFragment extends ViewPagerBaseFragment {

    private EduSohoListView mListView;
    public static final String IDS = "ids";

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_teacher_layout);
    }

    @Override
    public EduSohoListView getListView() {
        return mListView;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mListView = (EduSohoListView) view.findViewById(R.id.list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);

        CourseTeacherAdapter adapter = new CourseTeacherAdapter(
                mActivity, R.layout.course_teacher_item_layout);
        mListView.setEmptyString(new String[] { "该课程没有教师" }, R.drawable.icon_empty_teacher);
        mListView.setAdapter(adapter);

        Bundle bundle = getArguments();
        Teacher[] teachers = (Teacher[]) bundle.getSerializable(IDS);
        mListView.pushData(Arrays.asList(teachers));
    }
}
