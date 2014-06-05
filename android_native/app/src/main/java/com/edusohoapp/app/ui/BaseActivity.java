package com.edusohoapp.app.ui;

import android.app.ActivityGroup;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;

import com.edusohoapp.app.EdusohoApp;
import com.edusohoapp.app.model.ErrorResult;
import com.edusohoapp.app.model.School;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.view.LoadDialog;
import com.edusohoapp.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

public class BaseActivity extends ActivityGroup {

    protected BaseActivity mActivity;
    protected Context mContext;
    protected EdusohoApp app;
    protected TextView mActionBarTitle;
    protected LinearLayout mActionBarBack;
    protected TextView mActionBarBackIcon;
    protected TextView mActionBarBackText;
    protected ViewGroup mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        app = (EdusohoApp) getApplication();
        app.setDisplay(this);
    }

    public String wrapUrl(String url, String... params)
    {
        if (params.length > 0) {
            for (String param : params) {
                url = url.replaceFirst("\\{[\\w\\W^\\/]+\\}", param);
            }
        }
        return url;
    }

    public void saveCurrentSchool(School school) {
        app.setCurrentSchool(school);
    }

    private String splitTitle(String title)
    {
        int length = title.length();
        if (length > 10) {
            return title.substring(0, 10) + "...";
        }
        return title;
    }

    protected void showEmptyLayout(final String text)
    {
        ViewStub emptyLayout = (ViewStub) findViewById(R.id.list_empty_stub);
        if (emptyLayout == null) {
            return;
        }
        emptyLayout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                TextView emptyText = (TextView) view.findViewById(R.id.list_empty_text);
                emptyText.setText(text);
            }
        });
        emptyLayout.inflate();
        return;
    }

    protected void showErrorLayout(final String text, final ListErrorListener listener)
    {
        ViewStub errorLayout = (ViewStub) findViewById(R.id.list_error_layout);
        errorLayout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                TextView errorText = (TextView) view.findViewById(R.id.list_error_text);
                errorText.setText(text);
                View errorBtn = view.findViewById(R.id.list_error_btn);
                errorBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.error(view);
                    }
                });
            }
        });
        errorLayout.inflate();
        return;
    }

    public void changeTitle(String title)
    {
        if (title != null) {
            mActionBarTitle.setText(splitTitle(title));
        }
    }

    public void setBackIcon(int iconId, String title, View.OnClickListener listener)
    {
        mActionBarBackText = (TextView) findViewById(R.id.actionbar_back_text);
        mActionBarBackIcon = (TextView) findViewById(R.id.actionbar_back_icon);
        mActionBarBackText.setText("");
        mActionBarBackIcon.setText(iconId);
        setBackMode(title, true, listener);
    }

    public void setBackMode(String title, boolean isShowBack, View.OnClickListener listener)
    {
        mActionBarTitle = (TextView) findViewById(R.id.actionbar_title);
        mActionBarBack = (LinearLayout) findViewById(R.id.actionbar_back);
        mActionBarTitle.setText(splitTitle(title));
        if (isShowBack) {
            //back
            if (listener == null) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                };
            }
            mActionBarBack.setOnClickListener(listener);
        } else {
            mActionBarBack.setVisibility(View.GONE);
        }
    }

    public void setMenu(int menuViewRes, MenuListener listener)
    {
        LinearLayout menuLayout = (LinearLayout) findViewById(R.id.actionbar_menu);
        View menuView = getLayoutInflater().inflate(menuViewRes, null);
        menuLayout.addView(menuView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        menuView.setLayoutParams(lp);
        listener.bind(menuView);
    }

    public static interface MenuListener {
        public void bind(View menuView);
    }

    public void longToast(String title)
    {
        Toast.makeText(mContext, title, Toast.LENGTH_LONG).show();
    }

    public void ajaxGetString(
            String url, final ResultCallback rcl, Object... params)
    {
        boolean isShowLoading = true;
        if (params.length > 0) {
            isShowLoading = (Boolean) params[0];
        }

        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.query.ajax(url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (loading != null) {
                    loading.dismiss();
                }
                int code = status.getCode();
                try {
                    ErrorResult result = app.gson.fromJson(
                            object, new TypeToken<ErrorResult>() {
                    }.getType());
                    if (result != null) {
                        longToast(result.error.message);
                        return;
                    }
                } catch (Exception e) {
                    //error
                }

                if (code != Const.OK) {
                    longToast("网络异常");
                    rcl.error(url, status);
                    return;
                }
                rcl.callback(url,object,status);
            }
        });
    }

    public void ajaxNormalGet(String url, final ResultCallback rcl)
    {
        app.query.ajax(url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                int code = status.getCode();
                try {
                    ErrorResult result = app.gson.fromJson(
                            object, new TypeToken<ErrorResult>() {
                    }.getType());
                    if (result != null) {
                        longToast(result.error.message);
                        return;
                    }
                } catch (Exception e) {
                    //result error
                }
                if (code != Const.OK) {
                    longToast("网络异常");
                    rcl.error(url, status);
                    return;
                }
                rcl.callback(url,object,status);
            }
        });
    }

    /**
     * @suju
     */
    public interface ListErrorListener
    {
        public void error(View errorBtn);
    }
}
