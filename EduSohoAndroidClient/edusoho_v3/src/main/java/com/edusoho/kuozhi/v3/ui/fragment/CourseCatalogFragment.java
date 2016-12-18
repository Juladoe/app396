package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.CourseCatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.FixHeightListView;
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

    public CourseCatalogFragment() {
    }

    public CourseCatalogFragment(String courseId) {
        this.mCourseId = courseId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_course_catalog);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        init(view);
        initCatalogue();
        initCache(view);
    }

    protected void init(View view) {
        super.initView(view);
        mRlSpace = (RelativeLayout) view.findViewById(R.id.rl_space);
        mLvCatalog = (FixHeightListView) view.findViewById(R.id.lv_catalog);
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        tvSpace.setOnClickListener(getCacheCourse());
    }

    private void initCatalogue() {
        RequestUrl requestUrl = app.bindNewUrl(Const.LESSON_CATALOG + "?courseId=" + mCourseId + "&token=" + app.token, true);
        requestUrl.heads.put("token", app.token);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCourseCatalogue = ((CourseActivity) getActivity()).parseJsonValue(response, new TypeToken<CourseCatalogue>() {
                });
                if (mCourseCatalogue != null) {
                    initLessonCatalog();
                }else {
                    CommonUtil.shortCenterToast(getActivity(), "该课程没有课时");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    public void initLessonCatalog(){
        if (!mIsJoin) {
            mRlSpace.setVisibility(View.GONE);
        }
        mAdapter = new CourseCatalogueAdapter(getActivity(), mCourseCatalogue, mIsJoin);
        mLvCatalog.setAdapter(mAdapter);
        mLvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.changeSelected(position);
                if ("0".equals(mCourseCatalogue.getLessons().get(position).getFree())) {
                    if (!mIsJoin) {
                        CommonUtil.shortCenterToast(getActivity(), getString(R.string.unjoin_course_hint));
                        return;
                    }
                    return;
                }
            }
        });
    }

    /**
     * 外部刷新界面
     */
    public void reFreshView(){
        if (!mIsJoin) {
            mRlSpace.setVisibility(View.GONE);
        }
        mAdapter.setCourseCatalogue(null);
        mAdapter.notifyDataSetChanged();
    }
    /**
     * 获取手机可用空间,该界面要先判断是否显示rlSpace
     */
    private void initCache(View view) {
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        TextView tvCourse = (TextView) view.findViewById(R.id.tv_course);
        tvSpace.setText("可用空间:\t" + " " + getRomAvailableSize());
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