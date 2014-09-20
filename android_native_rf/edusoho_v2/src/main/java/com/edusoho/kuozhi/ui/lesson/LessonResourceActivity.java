package com.edusoho.kuozhi.ui.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LessonMaterialAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.BaseResult;
import com.edusoho.kuozhi.model.LessonMaterial;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.ListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-16.
 */
public class LessonResourceActivity extends ActionBarBaseActivity {

    private ListWidget mResourceListView;
    private CheckBox mSelectAllBtn;

    private int mCourseId;
    private int mLessonId;

    private LessonMaterialAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_resource_layout);
        initView();
    }

    private void initView()
    {
        setBackMode(BACK, "课时资料");
        mSelectAllBtn = (CheckBox) findViewById(R.id.lesson_resource_all);
        mResourceListView = (ListWidget) findViewById(R.id.lesson_resource_list);

        Intent data = getIntent();
        if (data != null) {
            mCourseId = data.getIntExtra(Const.COURSE_ID, 0);
            mLessonId = data.getIntExtra(Const.LESSON_ID, 0);
        }

        if (mCourseId == 0 || mLessonId == 0) {
            longToast("课程信息错误！");
            return;
        }

        loadResources();

        mSelectAllBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAdapter.setCheckAllStatus(b);
            }
        });
    }

    private void loadResources()
    {
        RequestUrl requestUrl = app.bindUrl(Const.LESSON_RESOURCE, true);
        requestUrl.setParams(new String[] {
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        ajaxPost(requestUrl, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                BaseResult<ArrayList<LessonMaterial>> lessonMaterialBaseResult = parseJsonValue(
                        object, new TypeToken<BaseResult<ArrayList<LessonMaterial>>>(){});

                if (lessonMaterialBaseResult == null) {
                    return;
                }

                mAdapter = new LessonMaterialAdapter(
                        mContext, lessonMaterialBaseResult.data, R.layout.lesson_material_item);
                mResourceListView.setAdapter(mAdapter);
            }
        });
    }
}
