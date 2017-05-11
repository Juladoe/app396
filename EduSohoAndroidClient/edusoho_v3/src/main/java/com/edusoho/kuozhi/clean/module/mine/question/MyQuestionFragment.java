package com.edusoho.kuozhi.clean.module.mine.question;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MineFragment;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;

/**
 * Created by JesseHuang on 2017/2/8.
 */

public class MyQuestionFragment extends BaseFragment implements MineFragment.RefreshFragment,MyQuestionContract.View{

    private static int ASK = 1;
    private static int ANSWER = 2;

    private SwipeRefreshLayout srlContent;
    private RecyclerView rvContent;
    private View rlayoutFilterType;
    private View llayoutFilterQuestionTypeList;
    private View viewCoverScreen;
    private TextView tvFilterName;
    private EduSohoNewIconView esivFilterArrow;
    private TextView tvAsk;
    private TextView tvAnswer;

    private MyAskQuestionAdapter mMyAskQuestionAdapter;
    private MyAnswerQuestionAdapter mMyAnswerQuestionAdapter;

    private MyQuestionPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_mine_tab);
    }

    @Override
    protected void initView(View view) {
        srlContent = (SwipeRefreshLayout) view.findViewById(R.id.srl_content);
        srlContent.setColorSchemeResources(R.color.primary_color);

        rvContent = (RecyclerView) view.findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));

        rlayoutFilterType = view.findViewById(R.id.rlayout_filter_type);
        rlayoutFilterType.setVisibility(View.VISIBLE);
        rlayoutFilterType.setOnClickListener(getShowTypeLayoutClickListener());

        llayoutFilterQuestionTypeList = view.findViewById(R.id.llayout_filter_question_type_list);
        llayoutFilterQuestionTypeList.setVisibility(View.GONE);
        llayoutFilterQuestionTypeList.bringToFront();

        viewCoverScreen = view.findViewById(R.id.view_cover_screen);
        viewCoverScreen.setOnClickListener(getCoverScreenClickListener());

        tvFilterName = (TextView) view.findViewById(R.id.tv_filter_name);

        esivFilterArrow = (EduSohoNewIconView) view.findViewById(R.id.tv_filter_arrow);
        tvAsk = (TextView) view.findViewById(R.id.tv_question_post);
        tvAnswer = (TextView) view.findViewById(R.id.tv_question_answer);
        tvAsk.setOnClickListener(getClickTypeClickListener());
        tvAnswer.setOnClickListener(getClickTypeClickListener());
        initData();
        srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switchFilterType(getCurrentType());
            }
        });
    }

    private void initData() {
        mMyAskQuestionAdapter = new MyAskQuestionAdapter(mContext);
        mMyAnswerQuestionAdapter = new MyAnswerQuestionAdapter(mContext);
        rvContent.setAdapter(mMyAskQuestionAdapter);
        mPresenter = new MyQuestionPresenter(this);
        switchFilterType(ASK);
    }

    private void switchFilterType(int type) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (type == ASK) {
            tvFilterName.setText(getString(R.string.question_post));
            loadAskedQuestionData();
            tvAsk.setTextColor(getResources().getColor(R.color.primary_color));
            tvAnswer.setTextColor(getResources().getColor(R.color.primary_font_color));
        } else if (type == ANSWER) {
            tvFilterName.setText(getString(R.string.question_answer));
            loadAnsweredQuestionData();
            tvAsk.setTextColor(getResources().getColor(R.color.primary_font_color));
            tvAnswer.setTextColor(getResources().getColor(R.color.primary_color));
        }
    }

    private void loadAskedQuestionData() {
        showLoadingView();
        mPresenter.requestAskData();
    }

    private void loadAnsweredQuestionData() {
        showLoadingView();
        mPresenter.requestAnswerData();
    }

    private int getCurrentType() {
        if (tvFilterName.getText().toString().equals(getString(R.string.question_post))) {
            return ASK;
        } else {
            return ANSWER;
        }
    }

    private View.OnClickListener getClickTypeClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_question_post) {
                    switchFilterType(ASK);
                } else if (v.getId() == R.id.tv_question_answer) {
                    MobclickAgent.onEvent(mContext, "i_myQuestionAndAnswer_iReplied");
                    switchFilterType(ANSWER);
                }
                llayoutFilterQuestionTypeList.setVisibility(View.GONE);
                esivFilterArrow.setText(getString(R.string.new_font_unfold));
            }
        };
    }

    private View.OnClickListener getShowTypeLayoutClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llayoutFilterQuestionTypeList.getVisibility() == View.VISIBLE) {
                    llayoutFilterQuestionTypeList.setVisibility(View.GONE);
                    esivFilterArrow.setText(getString(R.string.new_font_unfold));
                } else {
                    llayoutFilterQuestionTypeList.setVisibility(View.VISIBLE);
                    esivFilterArrow.setText(getString(R.string.new_font_fold));
                }
            }
        };
    }

    private View.OnClickListener getCoverScreenClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llayoutFilterQuestionTypeList.setVisibility(View.GONE);
                esivFilterArrow.setText(getString(R.string.new_font_unfold));
            }
        };
    }

    @Override
    public void refreshData() {
        initData();
        switchFilterType(ASK);
    }

    @Override
    public void setSwipeEnabled(int i) {
        srlContent.setEnabled(i == 0);
    }

    private void showLoadingView() {
        srlContent.post(new Runnable() {
            @Override
            public void run() {
                srlContent.setRefreshing(true);
            }
        });
    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showAskComplete(MyThreadEntity[] myThreadEntities) {
        hideSwp();
        mMyAskQuestionAdapter.setData(Arrays.asList(myThreadEntities));
        rvContent.setAdapter(mMyAskQuestionAdapter);
    }

    @Override
    public void showAnswerComplete(MyThreadEntity[] entities) {
        hideSwp();
        mMyAnswerQuestionAdapter.setData(Arrays.asList(entities));
        rvContent.setAdapter(mMyAnswerQuestionAdapter);
    }

    @Override
    public void hideSwp() {
        srlContent.setRefreshing(false);
    }

    public static class ViewHolderAsk extends RecyclerView.ViewHolder {
        TextView tvType;
        TextView tvContent;
        TextView tvTime;
        TextView tvReviewNum;
        TextView tvOrder;
        public View layout;
        View vLine;

        ViewHolderAsk(View view) {
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
        View vLine;
        public TextView tvTime;
        public TextView tvContentAnswer;
        public TextView tvContentAsk;
        public TextView tvOrder;
        public View layout;

        public ViewHolderAnswer(View view) {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvContentAnswer = (TextView) view.findViewById(R.id.tv_content_answer);
            tvContentAsk = (TextView) view.findViewById(R.id.tv_content_ask);
            tvOrder = (TextView) view.findViewById(R.id.tv_order);
            layout = view.findViewById(R.id.rlayout_answer_question_item_layout);
            vLine = view.findViewById(R.id.v_line);
        }
    }
}
