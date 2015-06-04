package com.edusoho.kuozhi.v3.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;
import com.edusoho.kuozhi.v3.view.EduSohoRoundedEditText;
import com.edusoho.kuozhi.v3.view.EduToolBar;

/**
 * Created by Melomelon on 2015/6/3.
 */
public class SearchDialogFragment extends DialogFragment {

    private EduSohoRoundedEditText mSearchFrame;
    private TextView mCancel;
    private ActionBarBaseActivity mActivity;
    private EduToolBar mEduToolBar;
    private Context mContext;

    private EdusohoApp mApp;

    private View view;

    private final int CANCEL_STATE = 0;
    private final int SEARCH_STATE = 1;

    private DialogInterface.OnDismissListener mOnDismissListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (ActionBarBaseActivity) activity;
        mContext = mActivity.getBaseContext();
        mApp = mActivity.app;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view  = inflater.inflate(R.layout.search_dialog,container,false);
        mSearchFrame = (EduSohoRoundedEditText) view.findViewById(R.id.search_dialog_frame);
        mSearchFrame.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        mCancel = (TextView) view.findViewById(R.id.cancel_search_btn);
        mCancel.setTag(CANCEL_STATE);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCancel.getTag() == CANCEL_STATE){
                    dismiss();
                }
                if(mCancel.getTag() == SEARCH_STATE){
                    //TODO
                    Toast.makeText(mContext,"搜索！",Toast.LENGTH_LONG).show();
                }

            }
        });
        searchListener();
        return view;
    }

    public void searchListener(){
        mSearchFrame.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    mCancel.setTag(SEARCH_STATE);
                    mCancel.setText("搜索");
                }
                if(s.length()==0){
                    mCancel.setTag(CANCEL_STATE);
                    mCancel.setText("取消");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void getToolBar(EduToolBar eduToolBar){
        this.mEduToolBar = eduToolBar;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mEduToolBar.getVisibility() == View.GONE){
        }

        mEduToolBar.measure(0, 0);
        int toolsBarHeight = mEduToolBar.getMeasuredHeight();
        ObjectAnimator animator = ObjectAnimator.ofInt(new EduSohoAnimWrap(mEduToolBar), "height", 0, toolsBarHeight);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        animator.start();

    }

}
