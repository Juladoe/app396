package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.MyAskAdapter;
import com.edusoho.kuozhi.v3.adapter.MyAskQuestionAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.edusoho.kuozhi.v3.model.provider.MyThreadProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/8.
 */

public class MyQuestionFragment extends BaseFragment {

    private RecyclerView rvFavorite;
    private View viewEmpty;
    private View rlayoutFilterType;
    private View llayoutFilterQuestionTypeList;
    private View viewCoverScreen;

    private MyThreadProvider mMyThreadProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine_tab);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void initView(View view) {
        viewEmpty = view.findViewById(R.id.view_empty);
        viewEmpty.setVisibility(View.GONE);

        rvFavorite = (RecyclerView) view.findViewById(R.id.rv_content);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getActivity()));

        rlayoutFilterType = view.findViewById(R.id.rlayout_filter_type);
        rlayoutFilterType.setVisibility(View.VISIBLE);
        rlayoutFilterType.setOnClickListener(getFilterTypeClickListener());

        llayoutFilterQuestionTypeList = view.findViewById(R.id.llayout_filter_question_type_list);
        llayoutFilterQuestionTypeList.bringToFront();

        viewCoverScreen = view.findViewById(R.id.view_cover_screen);
        viewCoverScreen.setOnClickListener(getCoverScreenClickListener());
    }

    private void initData() {
        mMyThreadProvider = new MyThreadProvider(mContext);
        loadAskQuestionData();
    }

    private void loadAskQuestionData() {
        RequestUrl requestUrl = EdusohoApp.app.bindNewUrl(Const.MY_CREATED_THREADS + "?start=0&limit=10000", true);
        final MyAskQuestionAdapter askQuestionAdapter = new MyAskQuestionAdapter(mContext);
        rvFavorite.setAdapter(askQuestionAdapter);
        mMyThreadProvider.getMyCreatedThread(requestUrl).success(new NormalCallback<MyThreadEntity[]>() {
            @Override
            public void success(MyThreadEntity[] entities) {
                askQuestionAdapter.addDatas(Arrays.asList(entities));
            }
        });
    }

    private void setNoCourseDataVisible(boolean visible) {
        if (visible) {
            viewEmpty.setVisibility(View.VISIBLE);
            rvFavorite.setVisibility(View.GONE);
        } else {
            viewEmpty.setVisibility(View.GONE);
            rvFavorite.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener getFilterTypeClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llayoutFilterQuestionTypeList.getVisibility() == View.VISIBLE) {
                    llayoutFilterQuestionTypeList.setVisibility(View.GONE);
                } else {
                    llayoutFilterQuestionTypeList.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private View.OnClickListener getCoverScreenClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }

    public static class ViewHolderAsk extends RecyclerView.ViewHolder {
        public TextView tvType;
        public TextView tvContent;
        public TextView tvTime;
        public TextView tvReviewNum;
        public TextView tvOrder;
        public View layout;
        View vLine;

        public ViewHolderAsk(View view) {
            super(view);
            tvType = (TextView) view.findViewById(R.id.tv_type);
            tvContent = (TextView) view.findViewById(R.id.tv_content);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvReviewNum = (TextView) view.findViewById(R.id.tv_review_num);
            tvOrder = (TextView) view.findViewById(R.id.tv_order);
            layout = view.findViewById(R.id.rlayout_ask_question_item_layout);
            vLine = view.findViewById(R.id.v_line);
        }
    }

    public static class ViewHolderAnswer extends RecyclerView.ViewHolder {
        TextView tvTime;
        HtmlTextView tvContentAnswer;
        TextView tvContentAsk;
        TextView tvOrder;
        View vLine;

        public ViewHolderAnswer(View view) {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvContentAnswer = (HtmlTextView) view.findViewById(R.id.tv_content_answer);
            tvContentAsk = (TextView) view.findViewById(R.id.tv_content_ask);
            tvOrder = (TextView) view.findViewById(R.id.tv_order);
            vLine = view.findViewById(R.id.v_line);
        }
    }
}
