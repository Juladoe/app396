package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-8-15.
 */
public class SearchDialogFragment extends DialogFragment{

    private View mCancelBtn;
    private EditText mSearchEdt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SearchDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_popwindow, container, false);

        mSearchEdt = (EditText) view.findViewById(R.id.search_popwindow_edt);
        mSearchEdt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_CLASS_TEXT);
        mSearchEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(null, "onFocusChange->" + b);
                if (b) {
                    Activity activity = getActivity();
                    InputMethodManager im = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
                    im.showSoftInput(mSearchEdt, InputMethodManager.RESULT_SHOWN);
                }
            }
        });
        mCancelBtn = view.findViewById(R.id.search_popwindow_cancel_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        EdusohoApp.app.sendMsgToTarget(FoundFragment.HIDE_ACTION_BAR_CODE, null, FoundFragment.class);
        super.onDestroy();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

        lp.width = EdusohoApp.screenW;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;
        lp.y = 0;

        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
