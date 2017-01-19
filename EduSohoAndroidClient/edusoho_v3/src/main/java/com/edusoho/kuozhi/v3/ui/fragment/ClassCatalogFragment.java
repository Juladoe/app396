package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.ClassCatalogueAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.provider.ClassRoomProvider;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import com.edusoho.kuozhi.v3.view.FixHeightListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by DF on 2016/12/15.
 */

public class ClassCatalogFragment extends BaseFragment {

    public boolean isJoin = false;
    public String mClassRoomId = "0";
    private FixHeightListView mLvClass;

    private View mLoadView;
    private List<Course> mCourseList;
    private LinearLayout mLessonEmpytView;

    public ClassCatalogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_catalog, container, false);
        mLvClass = (FixHeightListView) view.findViewById(R.id.lv_catalog);
        mLoadView = view.findViewById(R.id.il_class_catalog_load);
        mLessonEmpytView = (LinearLayout) view.findViewById(R.id.ll_course_catalog_empty);
        return view;
    }

    protected void setLoadStatus(int visibility) {
        mLoadView.setVisibility(visibility);
    }

    private void initData() {
        mClassRoomId = getArguments().getString("id");
        setLoadStatus(View.VISIBLE);
        new ClassRoomProvider(getContext()).getCourseList(AppUtil.parseInt(mClassRoomId))
        .success(new NormalCallback<List<Course>>() {
            @Override
            public void success(List<Course> list) {
                mCourseList = list;
                setLoadStatus(View.GONE);
                if (mCourseList != null && !mCourseList.isEmpty()) {
                    saveCourseListToCache(mCourseList);
                    initView();
                } else {
                    setLessonEmptyViewVisibility(View.VISIBLE);
                }
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                setLoadStatus(View.GONE);
            }
        });
    }

    private void saveCourseListToCache(List<Course> list) {
        StringBuilder sb = new StringBuilder();
        for (Course course : list) {
            sb.append(course.id).append(",");
        }
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());
        sqliteUtil.saveLocalCache(
                Const.CACHE_CLASSROOM_COURSE_IDS_TYPE,
                String.format("classroom-%s", mClassRoomId),
                sb.toString()
        );
    }

    private String getClassRoomName(int classRoomId) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(getContext());
        Classroom classroom = sqliteUtil.queryForObj(
                new TypeToken<Classroom>() {
                },
                "where type=? and key=?",
                Const.CACHE_COURSE_TYPE,
                "classroom-" + classRoomId
        );
        if (classroom != null) {
            return classroom.title;
        }
        return "";
    }

    private void initView() {
        ClassCatalogueAdapter classAdapter = new ClassCatalogueAdapter(getActivity(), mCourseList, isJoin);
        mLvClass.setAdapter(classAdapter);
        mLvClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (TextUtils.isEmpty(app.token)) {
                    CoreEngine.create(getContext()).runNormalPlugin("LoginActivity", getContext(), null);
                    return;
                }
                if (!isJoin && mCourseList.get(position).price > 0) {
                    CommonUtil.shortCenterToast(getActivity(), getString(R.string.class_catalog_join));
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(CourseActivity.COURSE_ID, String.valueOf(mCourseList.get(position).id));
                bundle.putString(CourseActivity.SOURCE, getClassRoomName(AppUtil.parseInt(mClassRoomId)));
                bundle.putBoolean(CourseActivity.IS_CHILD_COURSE, true);
                CoreEngine.create(getContext()).runNormalPluginWithBundle("CourseActivity", getContext(), bundle);
            }
        });

        mLvClass.setOnTouchListener(new View.OnTouchListener() {
            private int downX;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(((int) event.getX()) - downX) > 0) {
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }

    public void reFreshView(boolean mJoin){
        isJoin = mJoin;
        initData();
    }

    private void setLessonEmptyViewVisibility(int visibility) {
        mLessonEmpytView.setVisibility(visibility);
    }
}
