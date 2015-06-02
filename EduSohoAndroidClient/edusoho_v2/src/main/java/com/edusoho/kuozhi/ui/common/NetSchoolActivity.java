package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAutoCompleteTextView;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.photo.SchoolSplashActivity;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author howzhi
 */
public class NetSchoolActivity extends ActionBarBaseActivity {

    private static String mTitle = "输入域名";
    private static final String SEARCH_HISTORY = "search_history";

    private EdusohoAutoCompleteTextView mSearchEdt;
    private View mSearchBtn;
    private ArrayList<String> mSchoolList;

    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netschool);
        initView();
        app.addTask("NetSchoolActivity", this);
    }

    public static void start(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, NetSchoolActivity.class);
        context.startActivity(intent);
    }

    /**
     *
     */
    private void initView() {

        setBackMode(BACK, mTitle);
        mSearchBtn = findViewById(R.id.normal_search_btn);
        mSearchEdt = (EdusohoAutoCompleteTextView) findViewById(R.id.school_url_edit);

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

    private void saveSearchHistory(String text) {
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        Editor editor = sp.edit();

        if (sp.contains(text)) {
            return;
        }
        editor.putString(text, "");
        editor.commit();
    }

    private void loadSchoolHistory() {
        mSchoolList = new ArrayList<String>();
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        Map<String, ?> schools = sp.getAll();
        for (String key : schools.keySet()) {
            mSchoolList.add(key);
        }

        setSearchEdtHistory(mSchoolList);
    }

    protected void setSearchEdtHistory(ArrayList<String> list) {
        ArrayAdapter adapter = new ArrayAdapter(
                mContext, R.layout.search_dropdown_item, list);

        mSearchEdt.setAdapter(adapter);
    }

    private void searchSchool(String searchStr) {
        if (TextUtils.isEmpty(searchStr)) {
            longToast("请输入搜索网校url");
            return;
        }

        String url = "http://" + searchStr + Const.VERIFYVERSION;

        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();

        RequestUrl requestUrl = new RequestUrl(url);
        ajaxGet(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    loading.dismiss();
                    SystemInfo info = app.gson.fromJson(
                            object, new TypeToken<SystemInfo>() {
                            }.getType());

                    if (info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {
                        PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                        return;
                    }

                    RequestUrl requestUrl = new RequestUrl(info.mobileApiUrl + Const.VERIFYSCHOOL);
                    ajaxGet(requestUrl, new ResultCallback() {
                        @Override
                        public void callback(String url, String object, AjaxStatus ajaxStatus) {
                            SchoolResult schoolResult = app.gson.fromJson(
                                    object, new TypeToken<SchoolResult>() {
                                    }.getType());

                            if (schoolResult == null) {
                                PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                                return;
                            }
                            School site = schoolResult.site;
                            if (!checkMobileVersion(site, site.apiVersionRange)) {
                                return;
                            }

                            showSchSplash(site.name, site.splashs);
                            app.setCurrentSchool(site);
                            app.removeToken();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    PopupDialog.createNormal(mContext, "错误信息", "没有搜索到网校").show();
                    loading.dismiss();
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                loading.dismiss();
                super.error(url, ajaxStatus);
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
        app.appFinish();
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
}
