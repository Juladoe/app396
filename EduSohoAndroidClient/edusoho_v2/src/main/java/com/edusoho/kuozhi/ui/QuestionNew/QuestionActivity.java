package com.edusoho.kuozhi.ui.QuestionNew;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;

import java.util.ArrayList;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionActivity extends ActionBarBaseActivity{
    private RefreshListWidget mQuestionList;
    private QuestionListAdapter mQuestionListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_main_layout);
        initView();
        initDatas();
    }

    private void initView() {
        mQuestionList = (RefreshListWidget) this.findViewById(R.id.question_list);
        mQuestionListAdapter = new QuestionListAdapter(this,R.layout.question_list_item_inflate);
        mQuestionList.setAdapter(mQuestionListAdapter);
        mQuestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT, "QuestionDetatilFragment");
                app.mEngine.runNormalPluginWithBundle("FragmentPageActivity",mActivity,bundle);
            }
        });
    }

    private void initDatas() {
        ArrayList<QuestionListData> datas = new ArrayList<QuestionListData>();
        QuestionListData data = new QuestionListData("我是问题的题目"
                ,"(5回答)"
                ,"我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答" +
                "我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答" +
                "我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答我是问题的回答"
                ,"我是课时标题"
                ,"刚刚");
        for(int i=0;i<10;i++){
            datas.add(data);
        }
        mQuestionListAdapter.addItems(datas);
    }
}
