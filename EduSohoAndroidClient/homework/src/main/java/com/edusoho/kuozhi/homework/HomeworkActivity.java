package com.edusoho.kuozhi.homework;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.homework.listener.IHomeworkQuestionResult;
import com.edusoho.kuozhi.homework.model.HomeWorkModel;
import com.edusoho.kuozhi.homework.model.HomeWorkQuestion;
import com.edusoho.kuozhi.homework.model.HomeworkProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.test.QuestionType;
import com.edusoho.kuozhi.v3.model.provider.ModelProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Melomelon on 2015/10/14.
 */
public class HomeworkActivity extends ActionBarBaseActivity implements IHomeworkQuestionResult {

    private static HomeworkActivity homeworkActivity;
    public static final String HOMEWORK_ID = "homeworkId";
    private int mHomeWorkId;
    private String mType;
    private List<HomeWorkQuestion> mHomeWorkQuestionList;
    private List<String> mAnswerList;
    private HomeworkProvider mHomeworkProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeworkActivity = this;
        Intent intent = getIntent();
        mType = intent.getStringExtra("homework");
        if (intent == null || !intent.hasExtra(HOMEWORK_ID)) {
            CommonUtil.longToast(
                    getBaseContext(),
                    HomeworkSummaryActivity.HOME_HORK.equals(mType) ? "获取作业数据错误" : "获取练习数据错误"
            );
            return;
        }

        mHomeWorkId = intent.getIntExtra(HOMEWORK_ID, 0);
        setBackMode(BACK, HomeworkSummaryActivity.HOME_HORK.equals(mType) ? "作业" : "练习");
        ModelProvider.init(getBaseContext(), this);
        initView();
    }

    private void initView() {
        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.HOMEWORK_CONTENT, mHomeWorkId), true);
        final LoadDialog loadDialog = LoadDialog.create(mActivity);
        loadDialog.show();
        mHomeworkProvider.getHomeWork(requestUrl).success(new NormalCallback<HomeWorkModel>() {
            @Override
            public void success(HomeWorkModel homeWorkModel) {
                coverQuestionList(homeWorkModel);
                Bundle bundle = new Bundle();
                bundle.putString(Const.ACTIONBAR_TITLE, HomeworkSummaryActivity.HOME_HORK.equals(mType) ? "作业题目" : "练习题目");
                loadFragment(bundle);
                loadDialog.dismiss();
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                loadDialog.dismiss();
            }
        });
    }

    private void coverQuestionList(HomeWorkModel homeWorkModel) {
        mHomeWorkQuestionList = new ArrayList<HomeWorkQuestion>();
        for (HomeWorkQuestion question : homeWorkModel.getItems()) {
            QuestionType type = QuestionType.value(question.getType());
            if (QuestionType.material == type) {
                List<HomeWorkQuestion> items = question.getItems();
                for (HomeWorkQuestion itemQuestion : items) {
                    itemQuestion.setParent(question);
                    mHomeWorkQuestionList.add(itemQuestion);
                }
                continue;
            }
            mHomeWorkQuestionList.add(question);
        }
    }

    @Override
    public List<HomeWorkQuestion> getQuestionList() {
        return mHomeWorkQuestionList;
    }

    protected void loadFragment(Bundle bundle) {
        try {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            Fragment fragment = Fragment.instantiate(getBaseContext(), "com.edusoho.kuozhi.homework.ui.fragment.HomeWorkQuestionFragment");
            fragment.setArguments(bundle);
            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.commit();
        } catch (Exception ex) {
            Log.d("HomeworkActivity", ex.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getSupportFragmentManager().getFragments().size() > 0) {
            getSupportFragmentManager().getFragments().get(0).onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homework_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homework_menu_card){
            app.mEngine.runNormalPluginWithBundle("HomeworkAnswerCardActivity",mContext,null);
        }
        return super.onOptionsItemSelected(item);
    }

    public static HomeworkActivity getInstance(){
        return homeworkActivity;
    }
}
