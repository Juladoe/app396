package com.edusoho.kuozhi.ui.fragment.course;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.CourseIntroductionAdapter;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.EduSohoListView;

import java.util.ArrayList;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseIntroductionFragment extends ViewPagerBaseFragment {

    private EduSohoListView mListView;
    public static final String TITLES = "titles";
    public static final String CONTENTS = "contents";

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public EduSohoListView getListView() {
        return mListView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.course_introduction_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mListView = (EduSohoListView) view.findViewById(R.id.list_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mListView.setLayoutManager(linearLayoutManager);

        CourseIntroductionAdapter adapter = new CourseIntroductionAdapter(
                mContext, R.layout.course_introduction_item_layout);
        mListView.setAdapter(adapter);

        Bundle bundle = getArguments();
        String[] titles = bundle.getStringArray(TITLES);
        String[] contents = bundle.getStringArray(CONTENTS);
        ArrayList<String[]> list = new ArrayList<String[]>();
        for (int i=0; i < titles.length; i++) {
            list.add(new String[] { titles[i], contents[i]});
        }

        mListView.pushData(list);
    }
}
