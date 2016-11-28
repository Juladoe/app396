package com.edusoho.kuozhi.v3.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.imserver.IMClient;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.SystemInfo;
import com.edusoho.kuozhi.v3.model.provider.NetSchoolProvider;
import com.edusoho.kuozhi.v3.model.result.SchoolResult;
import com.edusoho.kuozhi.v3.model.sys.Error;
import com.edusoho.kuozhi.v3.model.sys.ErrorResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.model.sys.Token;
import com.edusoho.kuozhi.v3.ui.QrSchoolActivity;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SchoolUtil;
import com.edusoho.kuozhi.v3.view.EdusohoAutoCompleteTextView;
import com.edusoho.kuozhi.v3.view.photo.SchoolSplashActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang
 */
public class NetSchoolDialog extends Dialog implements Response.ErrorListener {
    private static final String SEARCH_HISTORY = "search_history";
    private static final int REQUEST_QR = 001;
    private static final int RESULT_QR = 002;
    private EdusohoAutoCompleteTextView mSearchEdt;
    protected LoadDialog mLoading;
    private ArrayList<String> mSchoolList;
    private ListView mListView;
    private List<Map<String, Object>> mList;
    private MyAdapter adapter;
    private TextView mtv;
    private View mCancel;
    private BaseActivity mContext;
    public EdusohoApp app;

    public NetSchoolDialog(Context context) {
        super(context, R.style.DialogFullscreen);
        init(context);
    }

    public NetSchoolDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    protected NetSchoolDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mContext = (BaseActivity) context;

        app = (EdusohoApp) mContext.getApplication();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_net_school);
        initView();
    }


    private void initView() {
        mCancel = findViewById(R.id.net_school_cancel_search_btn);
        mtv = (TextView) findViewById(R.id.net_school_tv);
        mSearchEdt = (EdusohoAutoCompleteTextView) findViewById(R.id.school_url_edit);
        mListView = (ListView) findViewById(R.id.net_school_listview);
        List<Map<String, Object>> list = SchoolUtil.loadEnterSchool(mContext);
        if (list != null && list.size() != 0) {
            Collections.reverse(list);
            mList = list;
        } else {
            mtv.setVisibility(View.GONE);
            mList = new ArrayList<>();
        }
        adapter = new MyAdapter(mContext);
        mListView.setAdapter(adapter);
        mSearchEdt.setKeyDownCallback(new EdusohoAutoCompleteTextView.KeyDownCallback() {
            @Override
            public void invoke(int length) {
                if (length < 1) {
                    return;
                }
                Editable text = mSearchEdt.getText();
                char input = text.charAt(length - 1);
                if (input == '.') {
                    if (text.toString().endsWith("www.")) {
                        return;
                    }
                    ArrayList<String> hisList = new ArrayList<String>();
                    hisList.add(mSearchEdt.getText() + "com");
                    hisList.add(mSearchEdt.getText() + "cn");
                    hisList.add(mSearchEdt.getText() + "net");
                    hisList.add(mSearchEdt.getText() + "org");
                    hisList.addAll(mSchoolList);
                    setSearchEdmContexttory(hisList);
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
                String searchStr = mSearchEdt.getText().toString();
                if(searchStr.length()>0){
                    new NetSchoolProvider(mContext).getNetSchool("").success(
                            new NormalCallback<Object>() {
                                @Override
                                public void success(Object obj) {

                                }
                            }
                    );
                }
//                saveSearchHistory(searchStr);
//                searchSchool(searchStr);

                return true;
            }
        });

        loadSchoolHistory();
    }

    private void loadSchoolHistory() {
        mSchoolList = new ArrayList<>();
        SharedPreferences sp = mContext.getSharedPreferences(SEARCH_HISTORY, Context.MODE_APPEND);
        Map<String, ?> mSet = sp.getAll();
        Map<String, ?> schools = sp.getAll();
        for (String key : schools.keySet()) {
            mSchoolList.add(key);
        }

        setSearchEdmContexttory(mSchoolList);
    }

    private void saveSearchHistory(String text) {
        SharedPreferences sp = mContext.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (sp.contains(text)) {
            return;
        }
        editor.putString(text, "");
        editor.commit();
    }

    protected void setSearchEdmContexttory(ArrayList<String> list) {
        ArrayAdapter adapter = new ArrayAdapter(
                mContext, R.layout.search_school_dropdown_item, list);

        mSearchEdt.setAdapter(adapter);
    }

    private void searchSchool(String searchStr) {
        if (TextUtils.isEmpty(searchStr)) {
            CommonUtil.longToast(mContext, "请输入网校url");
            return;
        }

        String url = "http://" + searchStr + Const.VERIFYVERSION;
        mLoading = LoadDialog.create(mContext);
        mLoading.show();

        RequestUrl requestUrl = new RequestUrl(url);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SystemInfo systemInfo = mContext.parseJsonValue(response, new TypeToken<SystemInfo>() {
                });

                if (systemInfo == null || TextUtils.isEmpty(systemInfo.mobileApiUrl)) {
                    mLoading.dismiss();
                    PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                    return;
                }

                getSchoolApi(systemInfo);
            }
        }, this);
    }

    private void handlerError(String errorStr) {
        try {
            ErrorResult result = app.gson.fromJson(errorStr, new TypeToken<ErrorResult>() {
            }.getType());
            if (result != null) {
                Error error = result.error;
                PopupDialog.createNormal(mContext, "系统提示", error.message).show();
            }
        } catch (Exception e) {
            PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
        }
    }


    private void showSchSplash(String schoolName, String[] splashs) {
        if (splashs == null || splashs.length == 0) {
            app.mEngine.runNormalPlugin("DefaultPageActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
            });
        }
        SchoolSplashActivity.start(mContext, schoolName, splashs);

        mContext.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        dismiss();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mLoading.dismiss();
        if (error.networkResponse == null) {
            CommonUtil.longToast(mContext, mContext.getResources().getString(R.string.request_failed));
        } else {
            CommonUtil.longToast(mContext, mContext.getResources().getString(R.string.request_fail_text));
        }
    }

    private void startSchoolActivity(School site) {
        mLoading.dismiss();
        showSchSplash(site.name, site.splashs);
    }

    protected void getSchoolApi(SystemInfo systemInfo) {
        final RequestUrl schoolApiUrl = new RequestUrl(systemInfo.mobileApiUrl + Const.VERIFYSCHOOL);
        app.getUrl(schoolApiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SchoolResult schoolResult = app.gson.fromJson(
                        response, new TypeToken<SchoolResult>() {
                        }.getType());

                if (schoolResult == null
                        || schoolResult.site == null) {
                    handlerError(response);
                    return;
                }

                School site = schoolResult.site;
                if (!SchoolUtil.checkMobileVersion(mContext, site, site.apiVersionRange)) {
                    return;
                }
                bindApiToken(site);
            }
        }, this);
    }

    private void saveSchoolHistory(School site) {
        SimpleDateFormat nowfmt = new SimpleDateFormat("登录时间：yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String loginTime = nowfmt.format(date);
        SchoolUtil.saveEnterSchool(mContext, site.name, loginTime, "登录账号：未登录", app.domain);
        startSchoolActivity(site);
    }

    protected void bindApiToken(final School site) {
        StringBuffer sb = new StringBuffer(site.host);
        sb.append(Const.GET_API_TOKEN);
        RequestUrl requestUrl = new RequestUrl(sb.toString());
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mLoading.dismiss();
                Token token = mContext.parseJsonValue(response, new TypeToken<Token>() {
                });
                if (token == null || TextUtils.isEmpty(token.token)) {
                    CommonUtil.longToast(mContext, "获取网校信息失败");
                    return;
                }
                app.setCurrentSchool(site);
                app.removeToken();
                app.registDevice(null);
                app.saveApiToken(token.token);
                getAppSettingProvider().setUser(null);
                IMClient.getClient().destory();
                saveSchoolHistory(site);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                app.setCurrentSchool(site);
                app.removeToken();
                app.registDevice(null);
                getAppSettingProvider().setUser(null);
                IMClient.getClient().destory();
                saveSchoolHistory(site);
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        Context context;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_net_school_dialog, null);
                holder.schoolTv = (TextView) convertView.findViewById(R.id.net_school_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.schoolTv.setText((String) mList.get(position).get("schoolname"));
            convertView.setTag(R.id.net_school_tv,position);
            convertView.setOnClickListener(mOnClickListener);
            return convertView;
        }
        private ViewHolder holder;
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.id.net_school_tv);
                HashMap map = (HashMap) mList.get(position);
                String schoolhost = map.get("schoolhost").toString();
                searchSchool(schoolhost);
            }
        };
    }

    private final class ViewHolder {
        public TextView schoolTv;
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        /**
         * 需解耦
         */
        if (mContext instanceof QrSchoolActivity) {
            ((QrSchoolActivity) mContext).onMessageEvent();
        }
    }
}

