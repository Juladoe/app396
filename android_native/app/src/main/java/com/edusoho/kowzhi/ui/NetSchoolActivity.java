package com.edusoho.kowzhi.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.adapter.RecommendSchoolAdapter;
import com.edusoho.kowzhi.entity.RecommendSchoolItem;
import com.edusoho.kowzhi.model.AppUpdateInfo;
import com.edusoho.kowzhi.model.School;
import com.edusoho.kowzhi.model.SchoolResult;
import com.edusoho.kowzhi.model.SystemInfo;
import com.edusoho.kowzhi.util.AppUtil;
import com.edusoho.kowzhi.util.Const;
import com.edusoho.kowzhi.view.EdusohoListView;
import com.edusoho.kowzhi.view.LoadDialog;
import com.edusoho.kowzhi.view.OverScrollView;
import com.edusoho.kowzhi.view.PopupDialog;
import com.edusoho.kowzhi.view.plugin.PopupLoaingDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.edusoho.listener.SchoolListClickListener;
import com.edusoho.plugin.photo.SchoolSplashActivity;
import com.edusoho.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

/**
 * @author howzhi
 */
public class NetSchoolActivity extends BaseActivity {

    private OverScrollView mNetSchLayout;

    private static String mTitle = "添加网校";
    private static final String SEARCH_HISTORY = "search_history";

    private ImageView mSearchQrBtn;
    private AutoCompleteTextView mSearchEdt;
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
        setBackMode(mTitle, true, null);
        mSearchBtn = findViewById(R.id.search_btn);
        mSearchEdt = (AutoCompleteTextView) findViewById(R.id.search_edt);
        mSearchQrBtn = (ImageView) findViewById(R.id.search_qr_btn);
        mNetSchLayout = (OverScrollView) findViewById(R.id.net_sch_layout);

        mSearchQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrIntent = new Intent();
                qrIntent.setClass(mContext, CaptureActivity.class);
                startActivityForResult(qrIntent, REQUEST_QR);
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

    private void saveSearchHistory(String text)
    {
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        Editor editor = sp.edit();
        Map<String, ?> allHistory = sp.getAll();
        int size = allHistory != null ? allHistory.size() : 0;
        editor.putString(size + "", text);
        editor.commit();
    }

    private void loadSchoolHistory()
    {
        mSchoolList = new ArrayList<String>();
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        Map<String, ?> schools = sp.getAll();
        for (String key : schools.keySet()) {
            mSchoolList.add(schools.get(key).toString());
        }

        ArrayAdapter adapter = new ArrayAdapter(
                mContext, R.layout.search_dropdown_item, mSchoolList);

        mSearchEdt.setAdapter(adapter);
    }

    private void searchSchool(String searchStr)
    {
        if (TextUtils.isEmpty(searchStr)) {
            longToast("请输入搜索网校url");
            return;
        }

        String url = "http://" + searchStr + Const.VERIFYVERSION;

        final LoadDialog loading = LoadDialog.create(mContext);
        loading.show();
        app.query.ajax(url, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                loading.dismiss();
                int code = status.getCode();
                if (code != Const.OK) {
                    PopupDialog.createNormal(mContext, "提示信息", "网络异常！请检查网络链接").show();
                    return;
                }

                try {
                    SystemInfo info = app.gson.fromJson(
                            object, new TypeToken<SystemInfo>() {
                    }.getType());

                    if (info.mobileApiUrl == null || "".equals(info.mobileApiUrl)) {
                        PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                        return;
                    }
                    ajaxNormalGet(info.mobileApiUrl + Const.VERIFYSCHOOL, new ResultCallback(){
                        @Override
                        public void callback(String url, String object, AjaxStatus ajaxStatus) {
                            super.callback(url, object, ajaxStatus);
                            SchoolResult schoolResult = app.gson.fromJson(
                                    object, new TypeToken<SchoolResult>() {
                            }.getType());

                            if (schoolResult == null) {

                            }
                            School site = schoolResult.site;
                            if (!checkMobileVersion(site.apiVersionRange)) {
                                return;
                            };

                            showSchSplash(site.name, site.splashs);
                            app.setCurrentSchool(site);
                        }
                    });

                } catch (Exception e) {
                    PopupDialog.createNormal(mContext, "错误信息", "没有搜索到网校").show();
                }
            }
        });
    }

    private void enterSchool(final School site)
    {
        StringBuilder message = new StringBuilder("正在进入...");
        message.append(site.name);

        PopupLoaingDialog.create(
                mContext,
                "搜索结果",
                message.toString(),
                new PopupLoaingDialog.PopupCallback() {
                    @Override
                    public void success() {
                        app.setCurrentSchool(site);
                        Intent courseIntent = new Intent(mContext,
                                SchCourseActivity.class);
                        startActivity(courseIntent);
                        finish();
                    }
                }).show();
    }

    private void showSchSplash(String schoolName, String[] splashs)
    {
        SchoolSplashActivity.start(mContext, schoolName, splashs);
        finish();
    }

    private boolean checkMobileVersion(HashMap<String, String> versionRange)
    {
        String min = versionRange.get("min");
        String max = versionRange.get("max");

        int result = AppUtil.compareVersion(app.apiVersion, min);
        if (result == Const.LOW_VERSIO) {
            PopupDialog dlg = PopupDialog.createMuilt(
                    mContext,
                    "网校提示",
                    "您的客户端版本过低，无法登录，请立即更新至最新版本。",
                    new PopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button) {
                            if (button == PopupDialog.OK) {
                                app.updateApp(true, new NormalCallback() {
                                    @Override
                                    public void success(Object obj) {
                                        AppUpdateInfo appUpdateInfo = (AppUpdateInfo) obj;
                                        app.startUpdateWebView(appUpdateInfo.updateUrl);
                                    }
                                });
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
                    "服务器维护中，请稍后再试。"
            ).show();
            return false;
        }

        return true;
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

    private void loadRecommendSchool(ArrayList<RecommendSchoolItem> list) {
        RecommendSchoolAdapter rsAdapter = new RecommendSchoolAdapter(
                mContext, list, R.layout.recommend_school_list_item);
        EdusohoListView refresh_listview = (EdusohoListView) findViewById(R.id.refresh_listview);

        refresh_listview.setAdapter(rsAdapter);
        refresh_listview.setOnItemClickListener(new SchoolListClickListener(this) {
            @Override
            public void afterClick(AdapterView<?> parent, int index) {
                RecommendSchoolItem item = (RecommendSchoolItem) parent.getItemAtPosition(index);
                saveRecommendSchool(item);
                finish();
            }
        });
    }

    private void saveRecommendSchool(RecommendSchoolItem item) {
        SharedPreferences sp = getSharedPreferences("recommend_school", MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(item.title, app.gson.toJson(item));
        edit.commit();
    }
}
