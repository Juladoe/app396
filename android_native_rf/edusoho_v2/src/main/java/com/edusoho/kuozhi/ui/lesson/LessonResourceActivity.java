package com.edusoho.kuozhi.ui.lesson;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.Service.DownLoadService;
import com.edusoho.kuozhi.adapter.LessonMaterialAdapter;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.BaseResult;
import com.edusoho.kuozhi.model.LessonMaterial;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

import com.edusoho.kuozhi.ui.widget.ListWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by howzhi on 14-9-16.
 */
public class LessonResourceActivity extends ActionBarBaseActivity
        implements MessageEngine.MessageCallback{

    private ListWidget mResourceListView;
    private ArrayList<LessonMaterial> mLessonMaterials;

    private int mCourseId;
    private int mLessonId;

    private LessonMaterialAdapter mAdapter;
    private File cacheDir;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INIT_STATUS:
                    initDownloadStatus();
            }
        }
    };

    public static final int INIT_STATUS = 0001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app.registMsgSource(this);
        cacheDir = AQUtility.getCacheDir(mContext);
        setContentView(R.layout.lesson_resource_layout);
        initView();
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        switch (type) {
            case INIT_STATUS:
                Log.d(null, "INIT_STATUS->");
                mHandler.obtainMessage(INIT_STATUS).sendToTarget();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(INIT_STATUS, source)
        };
        return messageTypes;
    }

    private void initView() {
        setBackMode(BACK, "课时资料");

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

        mResourceListView.setOnItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LessonMaterial lessonMaterial = (LessonMaterial) adapterView.getItemAtPosition(i);
                ArrayList<LessonMaterialAdapter.MaterialCKStatus> checkList = mAdapter.getCheckList();
                LessonMaterialAdapter.MaterialCKStatus status = checkList.get(i - 1);
                if (status == LessonMaterialAdapter.MaterialCKStatus.UN_DOWNLOAD) {
                    downLoadRes(lessonMaterial);
                    return;
                }
                File file = new File(cacheDir, lessonMaterial.title);
                Intent intent = AppUtil.getViewFileIntent(file);
                Log.d(null, "view file->" + intent);
                try {
                    startActivity(intent);
                }catch (Exception e) {
                    longToast("手机没有安装可查看软件！");
                }
            }
        });
    }

    private void downLoadRes(LessonMaterial lessonMaterial) {
        String url = String.format(
                Const.DOWNLOAD_MATERIAL,
                app.schoolHost,
                mCourseId,
                lessonMaterial.id,
                app.token
        );
        lessonMaterial.fileUri = url;
        DownLoadService.startDown(mContext, lessonMaterial, url);
    }

    private void initDownloadStatus()
    {
        if (mLessonMaterials == null) {
            return;
        }

        if (cacheDir == null || !cacheDir.exists()) {
            return;
        }

        ArrayList<Boolean> downloadStatus = new ArrayList<Boolean>();
        for (LessonMaterial material : mLessonMaterials) {
            File file = new File(cacheDir, material.title);
            Log.d(null, "file->" + file + "  " + file.exists());
            downloadStatus.add(file.exists());
        }
        mAdapter.initDownloadStatus(downloadStatus);
    }

    private void loadResources() {
        RequestUrl requestUrl = app.bindUrl(Const.LESSON_RESOURCE, true);
        requestUrl.setParams(new String[]{
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                BaseResult<ArrayList<LessonMaterial>> lessonMaterialBaseResult = parseJsonValue(
                        object, new TypeToken<BaseResult<ArrayList<LessonMaterial>>>() {
                });

                if (lessonMaterialBaseResult == null) {
                    return;
                }

                mLessonMaterials = lessonMaterialBaseResult.data;
                mAdapter = new LessonMaterialAdapter(
                        mContext, lessonMaterialBaseResult.data, R.layout.lesson_material_item);
                mResourceListView.setAdapter(mAdapter);
                initDownloadStatus();
            }
        });
    }

}
