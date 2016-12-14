package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

import java.io.File;

/**
 * Created by DF on 2016/12/13.
 */
public class CourseCatalogFragment extends Fragment {

    private View view;
    private RelativeLayout rlSpace;
    private RecyclerView rv;

    public CourseCatalogFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_class_catelog, container, false);
        rlSpace = (RelativeLayout) view.findViewById(R.id.rl_space);
        rv = (RecyclerView) view.findViewById(R.id.rv_catalog);

        initCache();

        return view;

    }

    /**
     * 获取手机可用空间,该界面要先判断是否显示rlSpace
     */
    private void initCache() {
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        TextView tvCourse = (TextView) view.findViewById(R.id.tv_course);
        tvSpace.setText("可用空间:"+" "+getRomAvailableSize());
        tvCourse.setOnClickListener(getCacheCourse());
    }

    public View.OnClickListener getCacheCourse(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * TODO 跳转到课程缓存界面
                  */
            }
        };
    }



    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(getActivity(), blockSize * availableBlocks).replace("B","");
    }
}
