package com.edusoho.kuozhi.clean.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;


/**
 * Created by JesseHuang on 2017/4/11.
 */

public class ESBottomDialog extends DialogFragment {

    private BottomDialogContentView mContentView;
    private TextView mConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_services, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout mContent = (FrameLayout) view.findViewById(R.id.content);
        mConfirm = (TextView) view.findViewById(R.id.tv_confirm);
        if (mContentView != null) {
            mContent.addView(mContentView.getContentView());
            mConfirm.setOnClickListener(mContentView.addConfirmClickListener());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setContent(BottomDialogContentView view) {
        mContentView = view;
    }

    public interface BottomDialogContentView {

        View getContentView();

        View.OnClickListener addConfirmClickListener();
    }
}
