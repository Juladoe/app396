package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.edusoho.kuozhi.v3.model.provider.MyThreadProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyAskAdapter extends BaseAdapter {

    private Context mContext;
    private int type = 0;
    private List<MyThreadEntity> mLists = new ArrayList<>();
    private boolean mEmpty = false;

    public MyAskAdapter(Context context, int type) {
        this.mContext = context;
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        return mEmpty ? mLists.size() == 0 && position == 0 ? 2 :
                type : type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return mEmpty && mLists.size() == 0 ? 1 : mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 2) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.view_empty, null, false);
            }
            return convertView;
        } else {
            if (getItemViewType(position) == 0) {
                convertView = buildAskView(position, convertView, parent);
            } else {
                convertView = buildAnswerView(position, convertView, parent);
            }
            return convertView;
        }
    }

    private View buildAskView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_ask1, null, false);
            viewHolderAsk = new ViewHolderAsk();
            viewHolderAsk.tvType = (TextView) convertView.findViewById(R.id.tv_type);
            viewHolderAsk.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolderAsk.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolderAsk.tvReviewNum = (TextView) convertView.findViewById(R.id.tv_review_num);
            viewHolderAsk.tvOrder = (TextView) convertView.findViewById(R.id.tv_order);
            viewHolderAsk.vLine = convertView.findViewById(R.id.v_line);
            convertView.setTag(viewHolderAsk);
        } else {
            viewHolderAsk = (ViewHolderAsk) convertView.getTag();
        }
        MyThreadEntity entity = mLists.get(position);
        if ("question".equals(entity.getType())) {
            viewHolderAsk.tvType.setText("问题");
            viewHolderAsk.tvType.setTextColor(mContext.getResources().getColor(R.color.primary_color));
            viewHolderAsk.tvType.setBackgroundResource(R.drawable.shape_ask_type_blue);
        } else {
            viewHolderAsk.tvType.setText("话题");
            viewHolderAsk.tvType.setTextColor(mContext.getResources().getColor(R.color.secondary2_color));
            viewHolderAsk.tvType.setBackgroundResource(R.drawable.shape_ask_type_red);
        }
        viewHolderAsk.tvContent.setText(Html.fromHtml("<html><body>&nbsp;&nbsp;&nbsp;" +
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + entity.getTitle() + "</body></html>"));
        viewHolderAsk.tvOrder.setText(entity.getCourse().title);
        viewHolderAsk.tvTime.setText(CommonUtil.getPostDays(entity.getCreatedTime()));
        viewHolderAsk.tvReviewNum.setText(entity.getPostNum());
        convertView.setTag(R.id.tv_order, position);
        convertView.setOnClickListener(mAskOnClickListener);
        if (position == getCount() - 1) {
            viewHolderAsk.vLine.setVisibility(View.GONE);
        }else{
            viewHolderAsk.vLine.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private View buildAnswerView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_ask2, null, false);
            viewHolderAnswer = new ViewHolderAnswer();
            viewHolderAnswer.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolderAnswer.tvContentAnswer = (HtmlTextView) convertView.findViewById(R.id.tv_content_answer);
            viewHolderAnswer.tvContentAsk = (TextView) convertView.findViewById(R.id.tv_content_ask);
            viewHolderAnswer.tvOrder = (TextView) convertView.findViewById(R.id.tv_order);
            viewHolderAnswer.vLine = convertView.findViewById(R.id.v_line);
            convertView.setTag(viewHolderAnswer);
        } else {
            viewHolderAnswer = (ViewHolderAnswer) convertView.getTag();
        }
        MyThreadEntity entity = mLists.get(position);
        viewHolderAnswer.tvOrder.setText(entity.getCourse().title);
        viewHolderAnswer.tvTime.setText(CommonUtil.getPostDays(entity.getCreatedTime()));
        viewHolderAnswer.tvContentAsk.setText(entity.getTitle());
        viewHolderAnswer.tvContentAnswer.setHtml(entity.getContent(),
                new HtmlHttpImageGetter(viewHolderAnswer.tvContentAnswer, null, true));
        convertView.setTag(R.id.tv_order, position);
        convertView.setOnClickListener(mAnswerOnClickListener);
        if (position == getCount() - 1) {
            viewHolderAnswer.vLine.setVisibility(View.GONE);
        }else{
            viewHolderAnswer.vLine.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private View.OnClickListener mAskOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startDiscussDetailActivity(v, true);
        }
    };


    private View.OnClickListener mAnswerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startDiscussDetailActivity(v, false);
        }
    };

    private MyThreadProvider mProvider;

    public void initData() {
        RequestUrl requestUrl;
        StringBuffer stringBuffer;
        mLists.clear();
        mEmpty = false;
        notifyDataSetChanged();
        switch (type) {
            case 0:
                requestUrl = EdusohoApp.app.bindNewUrl(Const.MY_CREATED_THREADS, true);
                stringBuffer = new StringBuffer(requestUrl.url);
                stringBuffer.append("?start=0&limit=10000/");
                requestUrl.url = stringBuffer.toString();

                mProvider = new MyThreadProvider(mContext);
                mProvider.getMyCreatedThread(requestUrl).success(new NormalCallback<MyThreadEntity[]>() {
                    @Override
                    public void success(MyThreadEntity[] entities) {
                        mLists.clear();
                        mLists.addAll(Arrays.asList(entities));
                        if (entities.length == 0) {
                            mEmpty = true;
                        }
                        notifyDataSetChanged();
                    }

                });
                break;
            case 1:
                requestUrl = EdusohoApp.app.bindNewUrl(Const.MY_POSTED_THREADS, true);
                stringBuffer = new StringBuffer(requestUrl.url);
                stringBuffer.append("?start=0&limit=10000/");
                requestUrl.url = stringBuffer.toString();

                mProvider = new MyThreadProvider(mContext);
                mProvider.getMyPostedThread(requestUrl).success(new NormalCallback<MyThreadEntity[]>() {
                    @Override
                    public void success(MyThreadEntity[] entities) {
                        mLists.clear();
                        mLists.addAll(Arrays.asList(entities));
                        if (entities.length == 0) {
                            mEmpty = true;
                        }
                        notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    private static ViewHolderAsk viewHolderAsk;
    private static ViewHolderAnswer viewHolderAnswer;

    private class ViewHolderAsk {
        TextView tvType;
        TextView tvContent;
        TextView tvTime;
        TextView tvReviewNum;
        TextView tvOrder;
        View vLine;
    }

    private class ViewHolderAnswer {
        TextView tvTime;
        HtmlTextView tvContentAnswer;
        TextView tvContentAsk;
        TextView tvOrder;
        View vLine;
    }

    public void setType(int type) {
        this.type = type;
        initData();
    }


    public void startDiscussDetailActivity(View v, boolean kind) {
        int position = (int) v.getTag(R.id.tv_order);
        final MyThreadEntity entity = mLists.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, "course");
        bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID,  entity.getCourse().id);
        bundle.putInt(AbstractIMChatActivity.FROM_ID, kind ? Integer.parseInt(entity.getId()) : Integer.parseInt(entity.getThreadId()));
        bundle.putString(AbstractIMChatActivity.TARGET_TYPE, entity.getType());
        EdusohoApp.app.mEngine.runNormalPluginWithBundle("DiscussDetailActivity", mContext, bundle);
    }

}
