package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.CourseCatalogueAdapter;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.kuozhi.v3.ui.LoginActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.FixHeightListView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.reflect.TypeToken;

import java.io.File;

/**
 * Created by DF on 2016/12/13.
 */
public class CourseCatalogFragment extends BaseFragment {
    public boolean mIsJoin = false;
    public String mCourseId;
    public CourseCatalogueAdapter mAdapter;
    private RelativeLayout mRlSpace;
    private FixHeightListView mLvCatalog;
    private CourseCatalogue mCourseCatalogue;
    private CourseCatalogue.LessonsBean mLessonsBean;
    private TextView tvSpace;
    private View view;
    private CourseCatalogue.LessonsBean lesson;

    public CourseCatalogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_catalog, container, false);
        mCourseId = getArguments().getString("courseid");
        init();
        initCatalogue();
        return view;
    }

    protected void init() {
        mRlSpace = (RelativeLayout) view.findViewById(R.id.rl_space);
        mLvCatalog = (FixHeightListView) view.findViewById(R.id.lv_catalog);
        tvSpace = (TextView) view.findViewById(R.id.tv_space);
        tvSpace.setOnClickListener(getCacheCourse());
    }

    private void initCatalogue() {
        if (mIsJoin && app.token != null) {
            mRlSpace.setVisibility(View.VISIBLE);
            initCache();
        }
        RequestUrl requestUrl = app.bindNewUrl(Const.LESSON_CATALOG + "?courseId=" + mCourseId + "&token=" + app.token, true);
        requestUrl.heads.put("token", app.token);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCourseCatalogue = ((CourseActivity) getActivity()).parseJsonValue(response, new TypeToken<CourseCatalogue>() {
                });
                if (mCourseCatalogue.getLessons().size() != 0) {
                    initLessonCatalog();
                    !TextUtils.isEmpty(app.token) &&
                } else {
                    CommonUtil.shortCenterToast(getActivity(), "该课程没有课时");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    public void initLessonCatalog() {
        mAdapter = new CourseCatalogueAdapter(getActivity(), mCourseCatalogue, mIsJoin);
        mLvCatalog.setAdapter(mAdapter);
        mLvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.changeSelected(position);
                if (TextUtils.isEmpty(app.token)) {
                    getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                if (!mIsJoin && "0".equals(mCourseCatalogue.getLessons().get(position).getFree())) {
                    CommonUtil.shortCenterToast(getActivity(), getString(R.string.unjoin_course_hint));
                    return;
                }
                startLessonActivity(position);
            }
        });
    }

    public void startLessonActivity(int position) {
        final LoadDialog loadDialog = LoadDialog.create(getActivity());
        lesson = mCourseCatalogue.getLessons().get(position);
        loadDialog.show();
        getSource(position);
    }

    //获取真实数据源
    public void getSource(int position){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSE_CHANGE_OBJECT, mCourseCatalogue.getLessons().get(position));
        if (mCourseCatalogue.getLearnStatuses().containsKey(mCourseCatalogue.getLessons().get(position).getId())) {
            if ("learning".equals(mCourseCatalogue.getLearnStatuses().get(mCourseCatalogue.getLessons().get(position).getId()))) {
                bundle.putString(Const.COURSE_CHANGE_STATE, "1");
            } else {
                bundle.putString(Const.COURSE_CHANGE_STATE, "2");
            }
        } else {
            bundle.putString(Const.COURSE_CHANGE_STATE, "0");
        }
        bundle.putBoolean(Const.COURSE_HASTRIAL_RESULT, true);
        new LessonProvider(mContext).getLesson(Integer.parseInt(lesson.getId())).success(new NormalCallback<LessonItem>() {
            @Override
            public void success(LessonItem obj) {

            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {

            }
        });
        MessageEngine.getInstance().sendMsg(Const.COURSE_HASTRIAL, bundle);
    }

    /**
     * 外部刷新数据
     */
    public void reFreshView(boolean mIsJoin) {
        this.mIsJoin = mIsJoin;
        if (mIsJoin && app.token != null) {
            mRlSpace.setVisibility(View.VISIBLE);
            initCache();
        }
        if (mAdapter != null) {
            mAdapter.isJoin = mIsJoin;
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取手机可用空间,该界面要先判断是否显示rlSpace
     */
    private void initCache() {
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        TextView tvCourse = (TextView) view.findViewById(R.id.tv_course);
        tvSpace.setText("可用空间: " + getRomAvailableSize());
        Log.d("test", getRomAvailableSize());
        tvCourse.setOnClickListener(getCacheCourse());
    }

    public View.OnClickListener getCacheCourse() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRomAvailableSize().contains("M")) {
                    if (Float.parseFloat(getRomAvailableSize().replaceAll("[a-zA-Z]", "").trim()) < 100) {
                        CommonUtil.shortCenterToast(getActivity(), getString(R.string.cache_hint));
                        return;
                    }
                }
                startActivity(new Intent(getContext(), LessonDownloadingActivity.class).putExtra(Const.COURSE_ID, Integer.parseInt(mCourseId)));
            }
        };
    }

    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(getActivity(), blockSize * availableBlocks).replace("B", "");
    }
}