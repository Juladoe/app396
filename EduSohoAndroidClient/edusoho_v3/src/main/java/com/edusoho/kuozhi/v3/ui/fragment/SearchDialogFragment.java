package com.edusoho.kuozhi.v3.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.Script;
import android.text.TextUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.friend.SearchFriendActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.EduSohoAnimWrap;
import com.edusoho.kuozhi.v3.view.EduSohoRoundedEditText;
import com.edusoho.kuozhi.v3.view.EduToolBar;

import java.util.Timer;
import java.util.TimerTask;

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

        view = inflater.inflate(R.layout.search_dialog, container, false);
        mSearchFrame = (EduSohoRoundedEditText) view.findViewById(R.id.search_dialog_frame);
        mSearchFrame.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        mSearchFrame.setCompoundDrawablePadding(20);

        mCancel = (TextView) view.findViewById(R.id.cancel_search_btn);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSearchFrame.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH){
                    searchFriend(mSearchFrame.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mSearchFrame.setFocusableInTouchMode(true);
        mSearchFrame.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
                public void run(){
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mSearchFrame, 0);
                }
        },300);
        return view;
    }

    public void searchFriend(final String searchStr){
        if(TextUtils.isEmpty(searchStr)){
            Toast.makeText(getActivity(), "请输入搜索内容！", Toast.LENGTH_SHORT).show();
            return;
        }else {
            mApp.mEngine.runNormalPlugin("SearchFriendActivity",mActivity,new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(SearchFriendActivity.NAME,searchStr);
                }
            });
        }
    }



    public void setToolBar(EduToolBar eduToolBar) {
        this.mEduToolBar = eduToolBar;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mEduToolBar.getVisibility() == View.GONE) {
        }

        int height = (Integer) mEduToolBar.getTag();

        ObjectAnimator animator = ObjectAnimator.ofInt(new EduSohoAnimWrap(mEduToolBar), "height", 0, height);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(300);
        animator.start();

    }

}
