package com.edusoho.kuozhi.v3.ui.fragment.mine;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.MyAskQuestionAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.thread.MyThreadEntity;
import com.edusoho.kuozhi.v3.model.provider.MyThreadProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.Arrays;

/**
 * Created by JesseHuang on 2017/2/8.
 */

public class MyQuestionFragment extends BaseFragment {

    private static int ASK = 1;
    private static int ANSWER = 2;

    private RecyclerView rvContent;
    private View viewEmpty;
    private View rlayoutFilterType;
    private View llayoutFilterQuestionTypeList;
    private View viewCoverScreen;
    private TextView tvFilterName;
    private EduSohoNewIconView esivFilterArrow;
    private TextView tvAsk;
    private TextView tvAnswer;

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
    }

    private void initData() {
        mMyThreadProvider = new MyThreadProvider(mContext);
        switchFilterType(ASK);
    }

    private void loadAskedQuestionData() {
        RequestUrl requestUrl = EdusohoApp.app.bindNewUrl(Const.MY_CREATED_THREADS + "?start=0&limit=10000", true);
        final MyAskQuestionAdapter askQuestionAdapter = new MyAskQuestionAdapter(mContext);
        rvContent.setAdapter(askQuestionAdapter);
        mMyThreadProvider.getMyCreatedThread(requestUrl).success(new NormalCallback<MyThreadEntity[]>() {
            @Override
            public void success(MyThreadEntity[] entities) {
                if (entities.length == 0) {
                    setNoCourseDataVisible(true);
                } else {
                    setNoCourseDataVisible(false);
                    askQuestionAdapter.addDatas(Arrays.asList(entities));
                }

            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError error) {
                setNoCourseDataVisible(true);
            }
        });
    }

    private void loadAnsweredQuestionData() {
        RequestUrl requestUrl = EdusohoApp.app.bindNewUrl(Const.MY_POSTED_THREADS + "?start=0&limit=10000", true);
        final MyAskQuestionAdapter askQuestionAdapter = new MyAskQuestionAdapter(mContext);
        rvContent.setAdapter(askQuestionAdapter);
        mMyThreadProvider.getMyCreatedThread(requestUrl).success(new NormalCallback<MyThreadEntity[]>() {
            @Override
            public void success(MyThreadEntity[] entities) {
                MyThreadEntity[] a = new MyThreadEntity[]{};
                if (a.length == 0) {
                    setNoCourseDataVisible(true);
                } else {
                    setNoCourseDataVisible(false);
                    askQuestionAdapter.addDatas(Arrays.asList(a));
                }
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError error) {
                setNoCourseDataVisible(true);
            }
        });
    }

    private void setNoCourseDataVisible(boolean visible) {
        if (visible) {
            viewEmpty.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
        } else {
            viewEmpty.setVisibility(View.GONE);
            rvContent.setVisibility(View.VISIBLE);
        }
        rlayoutFilterType.setVisibility(View.VISIBLE);
        rlayoutFilterType.bringToFront();
    }

    private void switchFilterType(int type) {
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

    private View.OnClickListener getClickTypeClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_question_post) {
                    switchFilterType(ASK);
                } else if (v.getId() == R.id.tv_question_answer) {
                    switchFilterType(ANSWER);
                }
                llayoutFilterQuestionTypeList.setVisibility(View.GONE);
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
        public TextView tvTime;
        public HtmlTextView tvContentAnswer;
        public TextView tvContentAsk;
        public TextView tvOrder;
        public View vLine;
        public View layout;

        public ViewHolderAnswer(View view) {
            super(view);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvContentAnswer = (HtmlTextView) view.findViewById(R.id.tv_content_answer);
            tvContentAsk = (TextView) view.findViewById(R.id.tv_content_ask);
            tvOrder = (TextView) view.findViewById(R.id.tv_order);
            layout = view.findViewById(R.id.rlayout_answer_question_item_layout);
            vLine = view.findViewById(R.id.v_line);
        }
    }
}
