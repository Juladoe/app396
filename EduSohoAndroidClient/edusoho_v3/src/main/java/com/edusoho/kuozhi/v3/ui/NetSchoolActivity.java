package com.edusoho.kuozhi.v3.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.model.bal.SystemInfo;
import com.edusoho.kuozhi.v3.model.result.SchoolResult;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoAutoCompleteTextView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.photo.SchoolSplashActivity;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JesseHuang on 15/5/28.
 */
public class NetSchoolActivity extends ActionBarBaseActivity {
    private static final String SEARCH_HISTORY = "search_history";
    private static final int REQUEST_QR = 001;
    private static final int RESULT_QR = 002;
    private EduSohoAutoCompleteTextView mSearchEdt;
    private View mSearchBtn;
    private ArrayList<String> mSchoolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_school);
        setBackMode(BACK, "输入域名");
        app.addTask("NetSchoolActivity", this);
        initView();
    }

    private void initView() {
        mSearchBtn = findViewById(R.id.normal_search_btn);
        mSearchEdt = (EduSohoAutoCompleteTextView) findViewById(R.id.school_url_edit);
        mSearchEdt.setKeyDownCallback(new EduSohoAutoCompleteTextView.KeyDownCallback() {
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
                    setSearchEdtHistory(hisList);
                }
            }
        });

        mSearchEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String searchStr = mSearchEdt.getText().toString();
                saveSearchHistory(searchStr);
                searchSchool(searchStr);
                return true;
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = mSearchEdt.getText().toString();
                saveSearchHistory(searchStr);
                searchSchool(searchStr);
            }
        });

        loadSchoolHistory();
    }

    private void loadSchoolHistory() {
        mSchoolList = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, Context.MODE_APPEND);
        Map<String, ?> mSet = sp.getAll();
        Map<String, ?> schools = sp.getAll();
        for (String key : schools.keySet()) {
            mSchoolList.add(key);
        }

        setSearchEdtHistory(mSchoolList);
    }

    private void saveSearchHistory(String text) {
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (sp.contains(text)) {
            return;
        }
        editor.putString(text, "");
        editor.commit();
    }

    protected void setSearchEdtHistory(ArrayList<String> list) {
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
        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();

        RequestUrl requestUrl = new RequestUrl(url);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                SystemInfo systemInfo = parseJsonValue(response, new TypeToken<SystemInfo>() {
                });

                if (TextUtils.isEmpty(systemInfo.mobileApiUrl)) {
                    PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                    return;
                }

                RequestUrl schoolApiUrl = new RequestUrl(systemInfo.mobileApiUrl + Const.VERIFYSCHOOL);
                app.getUrl(schoolApiUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SchoolResult schoolResult = app.gson.fromJson(
                                response, new TypeToken<SchoolResult>() {
                                }.getType());

                        if (schoolResult == null) {
                            PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                            return;
                        }
                        final School site = schoolResult.site;
                        if (!checkMobileVersion(site, site.apiVersionRange)) {
                            return;
                        }

//                        app.setCurrentSchool(site);
//                        app.removeToken();
//                        showSchSplash(site.name, site.splashs);

                        app.setCurrentSchool(site);
                        app.removeToken();
                        showSchSplash(site.name, site.splashs);

//                        RequestUrl requestUrl = app.bindUrl(Const.GET_API_TOKEN, false);
//                        app.postUrl(requestUrl, new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // TODO save apitoken
//                                app.saveApiToken(response);
//
//                                app.registDevice(new NormalCallback() {
//                                    @Override
//                                    public void success(Object obj) {
//                                        app.setCurrentSchool(site);
//                                        app.removeToken();
//                                        showSchSplash(site.name, site.splashs);
//                                    }
//                                });
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                CommonUtil.longToast(mContext, "无法获取网校Token");
//                            }
//                        });

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error.networkResponse == null) {
                            CommonUtil.longToast(mActivity, "无网络连接或请求失败");
                        } else {
                            CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                if (error.networkResponse == null) {
                    CommonUtil.longToast(mActivity, "无网络连接或请求失败");
                } else {
                    CommonUtil.longToast(mContext, getResources().getString(R.string.request_fail_text));
                }
            }
        });
    }

    public boolean checkMobileVersion(final School site, HashMap<String, String> versionRange) {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        int result = AppUtil.compareVersion(app.apiVersion, min);
        if (result == Const.LOW_VERSIO) {
            PopupDialog dlg = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录该网校，请立即更新至最新版本。",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                String code = getResources().getString(R.string.app_code);
                                String updateUrl = String.format(
                                        "%s/%s?code=%s",
                                        site.url,
                                        Const.DOWNLOAD_URL,
                                        code
                                );
                                app.startUpdateWebView(updateUrl);
                            }
                        }
                    });

            dlg.setOkText("立即下载");
            dlg.show();
            return false;
        }

        result = AppUtil.compareVersion(app.apiVersion, max);
        if (result == Const.HEIGHT_VERSIO) {
            PopupDialog.createNormal(
                    mContext,
                    "网校提示",
                    "网校服务器版本过低，无法继续登录！请重新尝试。"
            ).show();
            return false;
        }

        return true;
    }

    private void showSchSplash(String schoolName, String[] splashs) {
        SchoolSplashActivity.start(mContext, schoolName, splashs);
        //app.appFinish();
    }

    private void showQrResultDlg(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dlg = builder.setTitle("扫描结果")
                .setMessage("二维码信息:" + result)
                .setNegativeButton("查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR && resultCode == RESULT_QR) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                int status = bundle.getInt("status");
                String result = bundle.getString("result");
                showQrResultDlg(result);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
