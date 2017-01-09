package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by DF on 2017/1/8.
 */

public class DiscussDetailMessageListFragment extends MessageListFragment {


    private View inputView;
    private RelativeLayout mRlReplayEdit;
    private EditText mEtContent;
    private RelativeLayout mRlReplay;

    @Override
    protected void initView(View view) {

        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(com.edusoho.kuozhi.imserver.R.id.rotate_header_list_view_frame);
        mMessageListView = (RecyclerView) view.findViewById(com.edusoho.kuozhi.imserver.R.id.listview);
//        mMessageInputView = new MessageInputView(getActivity());
        ViewGroup inputViewGroup = (ViewGroup) view.findViewById(com.edusoho.kuozhi.imserver.R.id.message_input_view);
        //此处可修改相应布局简化
//        inputViewGroup.addView((View) mMessageInputView);
        inputView = LayoutInflater.from(getActivity()).inflate(R.layout.discuss_message_input_view, null);
        inputViewGroup.addView(inputView);
        mRlReplayEdit = (RelativeLayout)inputView.findViewById(R.id.rl_replay_edit);
        mRlReplay = (RelativeLayout) inputView.findViewById(R.id.rl_replay);
        mEtContent = (EditText)inputView.findViewById(R.id.et_content);
        inputView.findViewById(R.id.rl_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlReplay.setVisibility(View.GONE);
                mRlReplayEdit.setVisibility(View.VISIBLE);
            }
        });
        inputView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
            }
        });
        inputView.findViewById(R.id.tv_issue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtContent.getText().toString())) {
                    CommonUtil.shortCenterToast(getActivity(), "内容不可为空");
                    return;
                }
                getMessageSendListener().onSendMessage(mEtContent.getText().toString());
                mEtContent.setText("");
                hideKeyBoard();
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mMessageListView.setLayoutManager(mLayoutManager);
        mMessageListView.setAdapter(mListAdapter);
        mMessageListView.setItemAnimator(null);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.refreshComplete();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIMessageListPresenter.insertMessageList();
                    }
                }, 350);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                return canRefresh() && canDoRefresh;
            }
        });

        setEnable(true);
//        mMessageSendListener = getMessageSendListener();
//        mMessageInputView.setMessageSendListener(mMessageSendListener);
//        mMessageInputView.setMessageControllerListener(getMessageControllerListener());
//        mMessageListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    checkCanAutoLoad(recyclerView);
//                }
//            }
//        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mListAdapter = new QuestionAnswerAdapter(getActivity());
//        ((QuestionAnswerAdapter) mListAdapter).addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.thread_discuss_head_layout, null));
    }

    public void hideKeyBoard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEtContent.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        mRlReplay.setVisibility(View.VISIBLE);
        mRlReplayEdit.setVisibility(View.GONE);
    }
}
