package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/9.
 */

public class MyAskQuestionAdapter extends RecyclerView.Adapter<MyQuestionFragment.ViewHolderAsk> {

    private Context mContext;
    private List<MyThreadEntity> mMyThreadEntities;

    public MyAskQuestionAdapter(Context context) {
        mContext = context;
        mMyThreadEntities = new ArrayList<>();
    }

    public void addDatas(List<MyThreadEntity> list) {
        mMyThreadEntities.addAll(list);
        notifyDataSetChanged();
    }

    @Override

    public MyQuestionFragment.ViewHolderAsk onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_ask_question, parent, false);
        return new MyQuestionFragment.ViewHolderAsk(view);
    }

    @Override
    public void onBindViewHolder(MyQuestionFragment.ViewHolderAsk viewHolder, int position) {
        final MyThreadEntity entity = mMyThreadEntities.get(position);
        if ("question".equals(entity.getType())) {
            viewHolder.tvType.setText("问题");
            viewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            viewHolder.tvType.setBackgroundResource(R.drawable.shape_ask_type_blue);
        } else {
            viewHolder.tvType.setText("话题");
            viewHolder.tvType.setTextColor(mContext.getResources().getColor(R.color.secondary2_color));
            viewHolder.tvType.setBackgroundResource(R.drawable.shape_ask_type_red);
        }
        viewHolder.tvContent.setText(Html.fromHtml("<html><body>&nbsp;&nbsp;&nbsp;" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + entity.getTitle() + "</body></html>"));
        viewHolder.tvOrder.setText(entity.getCourse().title);
        viewHolder.tvTime.setText(CommonUtil.convertMills2Date(Long.parseLong(entity.getCreatedTime()) * 1000));
        viewHolder.tvReviewNum.setText(entity.getPostNum());
        viewHolder.layout.setTag(mMyThreadEntities.get(position));
        viewHolder.layout.setOnClickListener(getQuestionClickListener());
    }

    @Override
    public int getItemCount() {
        if (mMyThreadEntities != null) {
            return mMyThreadEntities.size();
        }
        return 0;
    }

    private View.OnClickListener getQuestionClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyThreadEntity entity = (MyThreadEntity) v.getTag();
                Bundle bundle = new Bundle();
                bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, "course");
                bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID, entity.getCourse().id);
                bundle.putInt(AbstractIMChatActivity.FROM_ID, Integer.parseInt(entity.getId()));
                bundle.putString(AbstractIMChatActivity.TARGET_TYPE, entity.getType());
                EdusohoApp.app.mEngine.runNormalPluginWithBundle("DiscussDetailActivity", mContext, bundle);
            }
        };
    }
}
