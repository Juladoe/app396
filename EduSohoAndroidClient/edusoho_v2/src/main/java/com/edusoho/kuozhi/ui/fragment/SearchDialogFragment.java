package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Testpaper.TagModel;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.course.CourseListActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-15.
 */
public class SearchDialogFragment extends DialogFragment {

    private TextView mCancelBtn;
    private EditText mSearchEdt;
    private View mClearBtn;
    private Context mContext;
    private EdusohoApp mApp;
    private ActionBarBaseActivity mActivity;
    private View view;

    private static final int SEARCH = 0;
    private static final int CANCEL = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SearchDialogTheme);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarBaseActivity) activity;
        mContext = mActivity.getBaseContext();
        mApp = mActivity.app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_popwindow, container, false);

        mSearchEdt = (EditText) view.findViewById(R.id.search_popwindow_edt);
        mSearchEdt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL | InputType.TYPE_CLASS_TEXT);

        mCancelBtn = (TextView) view.findViewById(R.id.search_popwindow_cancel_btn);
        mClearBtn = view.findViewById(R.id.search_clear_btn);

        bindViewListener();
        //loadTags();
        view.findViewById(R.id.rl_tags).setVisibility(View.GONE);
        return view;
    }

    /**
     * 获取热门标签数据
     */
    private void loadTags() {
        RequestUrl url = mApp.bindUrl(Const.GET_TAGS, false);
        ResultCallback callback = new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    ArrayList<TagModel> tagModels = mActivity.parseJsonValue(object, new TypeToken<ArrayList<TagModel>>() {
                    });
                    if (tagModels == null) {
                        return;
                    }
                    initTagsView(tagModels);
                } catch (Exception ex) {

                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
            }
        };
        mActivity.ajaxPost(url, callback);
    }

    /**
     * 标签点击事件
     */
    private View.OnClickListener tagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            final String courseTitle = tv.getText().toString();
            final String id = (String) v.getTag();
            EdusohoApp.app.mEngine.runNormalPlugin(
                    "CourseListActivity",
                    getActivity(),
                    new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(CourseListActivity.TITLE, courseTitle);
                            startIntent.putExtra(CourseListActivity.TAG_ID, id);
                        }
                    });
        }
    };

    /**
     * 排列热门标签
     */
    private void initTagsView(ArrayList<TagModel> tagList) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_tags);
        //************* 添加一行LinearLayout
        LinearLayout tmpLinearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tmpLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(tmpLinearLayout, linearLayoutParams);
        //*************

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int tagMargin = AppUtil.dip2px(mContext, 12);
        params.setMargins(tagMargin, tagMargin, tagMargin, tagMargin);
        float curScreenWid = EdusohoApp.screenW - tagMargin;

        for (TagModel tagModel : tagList) {
            ESTextView textView = new ESTextView(mContext);
            textView.changeAlpha(0.87f);
            textView.setText(tagModel.name);
            textView.setTag(tagModel.id);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(AppUtil.px2sp(mContext, getResources().getDimension(R.dimen.middle_font_size)));
            textView.setBackgroundResource(R.drawable.search_tag_btn_bg);
            textView.measure(0, 0);
            float wid = textView.getMeasuredWidth();
            if (curScreenWid > wid + tagMargin * 2) {

            } else {
                curScreenWid = EdusohoApp.screenW - tagMargin;
                LinearLayout newLinearLayout = new LinearLayout(mContext);
                tmpLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(newLinearLayout, linearLayoutParams);
                tmpLinearLayout = newLinearLayout;
            }
            curScreenWid = curScreenWid - wid - tagMargin * 2;
            textView.setOnClickListener(tagClickListener);
            tmpLinearLayout.addView(textView, params);
        }
    }


    private void bindViewListener() {
        mSearchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0 && mClearBtn.getVisibility() == View.GONE) {
                    mClearBtn.setVisibility(View.VISIBLE);
                    mCancelBtn.setTag(SEARCH);
                    mCancelBtn.setText("搜索");
                    return;
                }
                if (charSequence.length() == 0) {
                    mCancelBtn.setTag(CANCEL);
                    mCancelBtn.setText("取消");
                    mClearBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
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
                Object tag = mCancelBtn.getTag();
                if (tag == null) {
                    dismiss();
                    return;
                }

                int type = (Integer) tag;
                if (type == SEARCH) {
                    searchCourse(mSearchEdt.getText().toString());
                } else {
                    dismiss();
                }
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdt.setText("");
            }
        });
    }

    private void searchCourse(final String searchStr) {
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
        final Dialog dialog = super.onCreateDialog(savedInstanceState);

        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

        lp.width = EdusohoApp.screenW;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x = 0;
        lp.y = 0;

        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Activity activity = getActivity();
                InputMethodManager im = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
                im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(null, "search ->stop");
    }
}
