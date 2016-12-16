package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.CourseCatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.Gson;

import java.io.File;

/**
 * Created by DF on 2016/12/13.
 */
public class CourseCatalogFragment extends BaseFragment {
    public boolean isJoin = true;
    public String courseId;
    public CourseCatalogueAdapter adapter;
    private View view;
    private RelativeLayout rlSpace;
    private ListView lvCatalog;
    private CourseCatalogue courseCatalogue;

    public CourseCatalogFragment() {
    }

    public CourseCatalogFragment(String courseId) {
        this.courseId = courseId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_catalog, container, false);
        init(view);
        initCatalogue();
        initCache();
        return view;
    }

    protected void init(View view) {
        super.initView(view);
        rlSpace = (RelativeLayout) view.findViewById(R.id.rl_space);
        lvCatalog = (ListView) view.findViewById(R.id.lv_catalog);
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        tvSpace.setOnClickListener(getCacheCourse());
    }

    private void initCatalogue() {
        RequestUrl requestUrl = app.bindNewUrl(Const.LESSON_CATALOG + "?courseId=" + courseId, false);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                courseCatalogue = new Gson().fromJson(response, CourseCatalogue.class);
                initLessonCatalog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    public void initLessonCatalog(){
        if (!isJoin) {
            rlSpace.setVisibility(View.GONE);
        }
        courseCatalogue.getLessons().addAll(courseCatalogue.getLessons());
        adapter = new CourseCatalogueAdapter(getActivity(), courseCatalogue, isJoin);
        lvCatalog.setAdapter(adapter);
        lvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelected(position);
                if ("0".equals(courseCatalogue.getLessons().get(position).getFree())) {
                    if (!isJoin) {
                        CommonUtil.shortCenterToast(getActivity(), getString(R.string.unjoin_course_hint));
                        return;
                    }
                    return;
                }
            }
        });
    };

    /**
     * 外部刷新界面
     */
    public void reFreshView(){
        if (!isJoin) {
            rlSpace.setVisibility(View.GONE);
        }
        adapter.setCourseCatalogue(null);
        adapter.notifyDataSetChanged();
    }
    /**
     * 获取手机可用空间,该界面要先判断是否显示rlSpace
     */
    private void initCache() {
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        TextView tvCourse = (TextView) view.findViewById(R.id.tv_course);
        tvSpace.setText("可用空间:\t" + " " + getRomAvailableSize());
        tvCourse.setOnClickListener(getCacheCourse());
    }

    public View.OnClickListener getCacheCourse() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LessonDownloadingActivity.class).putExtra(Const.COURSE_ID, Integer.parseInt(courseId)));
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