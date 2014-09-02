package com.edusoho.kuozhi.ui.common;

import java.util.ArrayList;
import java.util.Map;

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

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.SchoolResult;
import com.edusoho.kuozhi.model.SystemInfo;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.course.SchoolCourseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAutoCompleteTextView;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.kuozhi.view.OverScrollView;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.kuozhi.view.dialog.PopupLoaingDialog;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.photo.SchoolSplashActivity;
import com.google.gson.reflect.TypeToken;

/**
 * @author howzhi
 */
public class NetSchoolActivity extends ActionBarBaseActivity {

    private OverScrollView mNetSchLayout;

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

    private void saveSearchHistory(String text)
    {
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        Editor editor = sp.edit();

        if (sp.contains(text)) {
            return;
        }
        editor.putString(text, "");
        editor.commit();
    }

    private void loadSchoolHistory()
    {
        mSchoolList = new ArrayList<String>();
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        Map<String, ?> schools = sp.getAll();
        for (String key : schools.keySet()) {
            mSchoolList.add(key);
        }

        setSearchEdtHistory(mSchoolList);
    }

    protected void setSearchEdtHistory(ArrayList<String> list)
    {
        ArrayAdapter adapter = new ArrayAdapter(
                mContext, R.layout.search_dropdown_item, list);

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
                                PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                                return;
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
                                SchoolCourseActivity.class);
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
