package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.ui.course.CourseListActivity;

/**
 * Created by howzhi on 14-8-15.
 */
public class SearchDialogFragment extends DialogFragment{

    private View mCancelBtn;
    private EditText mSearchEdt;
    private View mClearBtn;

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

        mCancelBtn = view.findViewById(R.id.search_popwindow_cancel_btn);
        mClearBtn = view.findViewById(R.id.search_clear_btn);
        bindViewListener();
        return view;
    }

    private void bindViewListener()
    {
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

        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0 && mClearBtn.getVisibility() == View.GONE) {
                    mClearBtn.setVisibility(View.VISIBLE);
                } else {
                    mClearBtn.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchCourse(mSearchEdt.getText().toString());
                return false;
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdt.setText("");
            }
        });
    }

    private void searchCourse(final String searchStr)
    {
        if (TextUtils.isEmpty(searchStr)) {
            Toast.makeText(getActivity(), "请输入搜索内容！", Toast.LENGTH_SHORT).show();
            return;
        }
        EdusohoApp.app.mEngine.runNormalPlugin(
                "CourseListActivity",
                getActivity(),
                new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(CourseListActivity.TITLE, searchStr);
                        startIntent.putExtra(CourseListActivity.SEARCH_TEXT, searchStr);
                    }
        });
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
