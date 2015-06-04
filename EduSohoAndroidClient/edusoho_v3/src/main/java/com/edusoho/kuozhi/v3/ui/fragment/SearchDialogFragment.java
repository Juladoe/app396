package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.view.EduSohoRoundedEditText;

import java.lang.reflect.Field;

/**
 * Created by Melomelon on 2015/6/3.
 */
public class SearchDialogFragment extends DialogFragment {

    private EduSohoRoundedEditText mSearchFrame;
    private TextView mCancel;
    private ActionBarBaseActivity mActivity;
    private Context mContext;

    private EdusohoApp mApp;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        return view;
    }


    public void showDialogFragment(String tag ){
        FragmentManager fm = getFragmentManager();
        this.show(fm, tag);
    }
}
