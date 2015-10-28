package com.edusoho.kuozhi.homework;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.homework.model.ExerciseModel;
import com.edusoho.kuozhi.homework.model.ExerciseProvider;
import com.edusoho.kuozhi.homework.model.ExerciseResult;
import com.edusoho.kuozhi.homework.model.HomeWorkResult;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

/**
 * Created by Melomelon on 2015/10/13.
 */
public class HomeworkSummaryActivity extends ActionBarBaseActivity {

    private static HomeworkSummaryActivity homeworkSummaryActivity;
    public static final String HOMEWORK = "homework";
    public static final String EXERCISE = "exercise";
    public static final String TYPE = "type";
    public static final int REQUEST_DO = 0010;

    private int mLessonId;
    private String mType;

    private Bundle mBundle;
    private HomeworkProvider mHomeworkProvider;
    private ExerciseProvider mExerciseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mBundle = intent.getExtras();

        mLessonId = mBundle == null ? 0 : mBundle.getInt(Const.LESSON_ID);
        mType = mBundle == null ? HOMEWORK : mBundle.getString("type");
        setBackMode(BACK, HOMEWORK.equals(mType) ? "作业" : "练习");
        setContentView(R.layout.homework_summary_layout);
        ModelProvider.init(getBaseContext(), this);
        initView();
    }

    public String getType() {
        return mType;
    }

    private void renderHomeworkView(final HomeWorkResult homeWorkResult) {
        String fragmentName = null;
        if (homeWorkResult == null || "doing".equals(homeWorkResult.status)) {
            fragmentName = "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkSummaryFragment";
        } else {
            fragmentName = "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkResultFragment";
        }

        Bundle bundle = getIntent().getExtras();
        loadFragment(bundle, fragmentName);
    }

    private void renderExerciseView() {
        String fragmentName = "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkSummaryFragment";
        Bundle bundle = getIntent().getExtras();
        loadFragment(bundle, fragmentName);
    }

    protected void loadFragment(Bundle bundle, String fragmentName) {
        try {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            Fragment fragment = Fragment.instantiate(getBaseContext(), fragmentName);
            fragment.setArguments(bundle);
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {
            Log.d("HomeworkSummaryActivity", ex.toString());
        }
    }

    public void initView() {
        if (HOMEWORK.equals(mType)){
            loadHomeWork();
        }else {
            renderExerciseView();
        }
    }

    private void loadHomeWork() {
        String url = String.format(Const.HOMEWORK_RESULT, mLessonId);
        RequestUrl requestUrl = app.bindNewUrl(url, true);
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();
        mHomeworkProvider.getHomeWorkResult(requestUrl, false).success(new NormalCallback<HomeWorkResult>() {
            @Override
            public void success(HomeWorkResult homeWorkModel) {
                loadDialog.dismiss();
                renderHomeworkView(homeWorkModel);
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                loadDialog.dismiss();
            }
        });
    }

//    private void loadExercise(){
//        String url = String.format(Const.EXERCISE_RESULT, mLessonId);
//        RequestUrl requestUrl = app.bindNewUrl(url, true);
//        final LoadDialog loadDialog = LoadDialog.create(mActivity);
//        loadDialog.show();
//        mExerciseProvider.getExerciseResult(requestUrl, false).success(new NormalCallback<ExerciseResult>() {
//            @Override
//            public void success(ExerciseResult exerciseResult) {
//                loadDialog.dismiss();
//                renderExerciseView(exerciseResult);
//            }
//        }).fail(new NormalCallback<VolleyError>() {
//            @Override
//            public void success(VolleyError obj) {
//                loadDialog.dismiss();
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (HOMEWORK.equals(mType)){
            if (requestCode == REQUEST_DO && resultCode == HomeworkActivity.RESULT_DO) {
                loadHomeWork();
            }
        }else {
            //todo
        }

    }

    public static HomeworkSummaryActivity getInstance(){
        return homeworkSummaryActivity;
    }
}
