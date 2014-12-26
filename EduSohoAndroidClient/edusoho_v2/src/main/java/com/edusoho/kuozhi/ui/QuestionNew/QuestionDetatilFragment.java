package com.edusoho.kuozhi.ui.QuestionNew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;


import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;

import java.util.ArrayList;

/**
 * Created by onewoman on 2014/12/22.
 */
public class QuestionDetatilFragment extends BaseFragment{
    private View mQuestionDetailDescribe;
    private RefreshListWidget mQuestionDetatileAnswerList;
    private QuestionDetatilAnswerListAdapter mQuestionDetatilAnswerListAdapter;
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.question_detatil_fragmentlayout);
    }

    @Override
    protected void initView(View view) {
        mQuestionDetailDescribe = view.findViewById(R.id.question_detatil_describe_inflate);
        setDataToquestionDetailDescribe();
        mQuestionDetatileAnswerList = (RefreshListWidget) view.findViewById(R.id.question_detail_answer_list);
        mQuestionDetatilAnswerListAdapter = new QuestionDetatilAnswerListAdapter(mContext,R.layout.question_detatil_answer_list_item);
        mQuestionDetatileAnswerList.setAdapter(mQuestionDetatilAnswerListAdapter);
        addQuestionDetatilListData();
        mQuestionDetatileAnswerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString(FragmentPageActivity.FRAGMENT,"QuestionReplyFragment");
                app.mEngine.runNormalPluginWithBundle("FragmentPageActivity",mActivity,bundle);
            }
        });
    }

    public void setDataToquestionDetailDescribe(){
        String content = "<p>\\n\\t<span style=\\\"color:#666666;font-family:'Microsoft YaHei', 'WenQuanYi Micro Hei', SimHei, tahoma, sans-serif;line-height:22.0000190734863px;background-color:#FFFFFF;\\\">汁的草莓可以保鲜更久？</span><span style=\\\"color:#666666;font-family:'Microsoft YaHei', 'WenQuanYi Micro Hei', SimHei, tahoma, sans-serif;line-height:22.0000190734863px;background-color:#FFFFFF;\\\">汁的草莓可以保鲜更久？</span><span style=\\\"color:#666666;font-family:'Microsoft YaHei', 'WenQuanYi Micro Hei', SimHei, tahoma, sans-serif;line-height:22.0000190734863px;background-color:#FFFFFF;\\\">汁的草莓可以保鲜更久？</span><span style=\\\"color:#666666;font-family:'Microsoft YaHei', 'WenQuanYi Micro Hei', SimHei, tahoma, sans-serif;line-height:22.0000190734863px;background-color:#FFFFFF;\\\">汁的草莓可以保鲜更久？</span><span style=\\\"color:#666666;font-family:'Microsoft YaHei', 'WenQuanYi Micro Hei', SimHei, tahoma, sans-serif;line-height:22.0000190734863px;background-color:#FFFFFF;\\\">汁的草莓可以保鲜更久？</span><img src=\\\"http://trymob.edusoho.cn/files/course/2014/12-17/1644171d1f28828451.png?4.3.2\\\" alt=\\\"\\\" /></p>\\n<p>\\n\\t<img src=\\\"http://trymob.edusoho.cn/files/course/2014/12-17/1644342357de188038.png?4.3.2\\\" alt=\\\"\\\" /> <img src=\\\"http://trymob.edusoho.cn/files/course/2014/12-17/164443b64a67974703.png?4.3.2\\\" alt=\\\"\\\" /></p>";
        QuestionDetatilDescribeData questionDetatilDescribeData = new QuestionDetatilDescribeData(
                "我是提问的标题",getResources().getDrawable(R.drawable.question_answer_icon),"joanie","2014-12-19",
                content,"太阳与月亮",233,22);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_describe_title)).setText(questionDetatilDescribeData.questionDetailTitle);
        ((ImageView) mQuestionDetailDescribe.findViewById(R.id.question_detatil_describe_user_head_image)).setImageDrawable(questionDetatilDescribeData.questionUserHeadImage);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detatil_describe_uesr_name)).setText(questionDetatilDescribeData.questionDetailUesrName);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detatile_describe_time)).setText(questionDetatilDescribeData.questionQuizTime);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detatil_describe_content)).setText("abc");
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_course_title)).setText(questionDetatilDescribeData.questionDetailCourseTitle);
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_describe_answer_count)).setText(questionDetatilDescribeData.questionDetailAnswerCount+"");
        ((TextView) mQuestionDetailDescribe.findViewById(R.id.question_detail_describe_browse_count)).setText(questionDetatilDescribeData.questionDetailCheckCount+"");
    }

    public void addQuestionDetatilListData(){
        ArrayList<QuestionDetatilAnswerListData> listDatas = new ArrayList<QuestionDetatilAnswerListData>();
        QuestionDetatilAnswerListData data = new QuestionDetatilAnswerListData(getResources().getDrawable(R.drawable.question_answer_icon),"joanie","2014-12-19","我是回答");
        for(int i=0;i<5;i++){
            listDatas.add(data);
        }
        mQuestionDetatilAnswerListAdapter.addItems(listDatas);
    }
}
