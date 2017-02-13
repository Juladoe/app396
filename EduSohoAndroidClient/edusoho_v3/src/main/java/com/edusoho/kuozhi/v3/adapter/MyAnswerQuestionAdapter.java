package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MyQuestionFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/9.
 */

public class MyAnswerQuestionAdapter extends RecyclerView.Adapter<MyQuestionFragment.ViewHolderAnswer> {
    private Context mContext;
    private List<MyThreadEntity> mMyThreadEntities;

    public MyAnswerQuestionAdapter(Context context) {
        mContext = context;
        mMyThreadEntities = new ArrayList<>();
    }

    public void setData(List<MyThreadEntity> list) {
        mMyThreadEntities = list;
        notifyDataSetChanged();
    }

    @Override
    public MyQuestionFragment.ViewHolderAnswer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_answer_question, parent, false);
        return new MyQuestionFragment.ViewHolderAnswer(view);
    }

    @Override
    public void onBindViewHolder(MyQuestionFragment.ViewHolderAnswer viewHolder, int position) {
        final MyThreadEntity entity = mMyThreadEntities.get(position);
        viewHolder.tvOrder.setText(entity.getCourse().title);
        viewHolder.tvTime.setText(CommonUtil.convertMills2Date(Long.parseLong(entity.getCreatedTime()) * 1000));
        viewHolder.tvContentAsk.setText(entity.getTitle());
        viewHolder.tvContentAnswer.setHtml(entity.getContent(), new HtmlHttpImageGetter(viewHolder.tvContentAnswer, null, true));
        viewHolder.layout.setOnClickListener(getAnswerClickListener());
    }

    @Override
    public int getItemCount() {
        if (mMyThreadEntities != null) {
            return mMyThreadEntities.size();
        }
        return 0;
    }

    private View.OnClickListener getAnswerClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThreadEntity entity = (MyThreadEntity) v.getTag();
                Bundle bundle = new Bundle();
                bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, "course");
                bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID, entity.getCourse().id);
                bundle.putInt(AbstractIMChatActivity.FROM_ID, Integer.parseInt(entity.getThreadId()));
                bundle.putString(AbstractIMChatActivity.TARGET_TYPE, entity.getType());
                EdusohoApp.app.mEngine.runNormalPluginWithBundle("DiscussDetailActivity", mContext, bundle);
            }
        };
    }
}
