package com.edusoho.kuozhi.homework;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.edusoho.kuozhi.homework.model.ExerciseProvider;
import com.edusoho.kuozhi.homework.model.HomeWorkModel;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.v3.listener.BaseLessonPluginCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.util.ApiTokenUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

/**
 * Created by howzhi on 15/11/2.
 */
public class ExerciseSummaryActivity extends HomeworkSummaryActivity {

    private ExerciseProvider mExerciseProvider;

    @Override
    public String getType() {
        return EXERCISE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        int exerciseId = data.getIntExtra(ExerciseActivity.EXERCISE_ID, 0);
        Bundle bundle = new Bundle();
        bundle.putInt(ExerciseParseActivity.EXERCISE_ID,exerciseId);
        app.mEngine.runNormalPluginWithBundle("ExerciseParseActivity",mContext,bundle);
    }

    @Override
    protected void initView() {
        setBackMode(BACK, "练习");
        renderExerciseView();
    }

    private void renderExerciseView() {
        String fragmentName = "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkSummaryFragment";
        Bundle bundle = getIntent().getExtras();
        bundle.putString(TYPE,"exercise");
        loadFragment(bundle, fragmentName);
    }

    public static class Callback extends BaseLessonPluginCallback
    {
        public Callback(Context context)
        {
            super(context);
        }

        @Override
        protected RequestUrl getRequestUrl(int lessonId) {
            String url = new StringBuilder()
                    .append(String.format(Const.EXERCISE_CONTENT, lessonId))
                    .append("?_idType=lesson")
                    .toString();
            return ApiTokenUtil.bindNewUrl(mContext, url, true);
        }

        @Override
        public boolean click(AdapterView<?> parent, View view, int position) {
            if (!view.isEnabled()) {
                CommonUtil.longToast(mContext, "课程暂无练习");
                return true;
            }
            return false;
        }

        @Override
        protected void loadPlugin(Bundle bundle) {
            int lessonId = bundle.getInt(Const.LESSON_ID, 0);
            RequestUrl requestUrl = getRequestUrl(lessonId);
            HomeworkProvider provider = ModelProvider.initProvider(mContext, HomeworkProvider.class);
            provider.getHomeWork(requestUrl).success(new NormalCallback<HomeWorkModel>() {
                @Override
                public void success(HomeWorkModel homeWorkModel) {
                    setViewStatus(homeWorkModel != null && homeWorkModel.getId() != 0);
                }
            }).fail(this);
        }
    }
}
