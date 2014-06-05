package com.edusohoapp.app.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.adapter.RecommendSchoolAdapter;
import com.edusohoapp.app.entity.CourseResult;
import com.edusohoapp.app.entity.RecommendSchoolItem;
import com.edusohoapp.app.entity.VerifySchoolItem;
import com.edusohoapp.app.model.ErrorResult;
import com.edusohoapp.app.model.SchoolResult;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.util.NetUtil;
import com.edusohoapp.app.util.NetUtil.modelAjaxCallback;
import com.edusohoapp.app.view.EdusohoListView;
import com.edusohoapp.app.view.EdusohoPullRrefreshView;
import com.edusohoapp.app.view.EdusohoPullRrefreshView.RefreshCallback;
import com.edusohoapp.app.view.LoadDialog;
import com.edusohoapp.app.view.OverScrollView;
import com.edusohoapp.app.view.PopupDialog;
import com.edusohoapp.app.view.plugin.PopupLoaingDialog;
import com.edusohoapp.handler.ProgressBarHandler;
import com.edusohoapp.listener.ResultCallback;
import com.edusohoapp.listener.SchoolListClickListener;
import com.edusohoapp.plugin.qr.CaptureActivity;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author howzhi
 *
 */
public class NetSchoolActivity extends BaseActivity implements SearchView.OnQueryTextListener{

    private OverScrollView net_sch_layout;
    private View load_layout;
    private Handler workHandler;
    private static String mTitle = "添加网校";
    private ImageView search_qr_btn;
    private EditText search_edt;
    private View search_btn;
    public final static int REQUEST_QR = 0001;
    public final static int RESULT_QR = 0002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.netschool);

        //搜索进度条handler
        workHandler = new ProgressBarHandler(this);
        initView();
        app.addTask("NetSchoolActivity", this);
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, NetSchoolActivity.class);
        context.startActivity(intent);
    }

    /**
     *
     */
    private void initView()
    {
        setBackMode(mTitle, true, null);
        search_btn = findViewById(R.id.search_btn);
        load_layout = findViewById(R.id.load_layout);
        search_edt = (EditText) findViewById(R.id.search_edt);
        search_qr_btn = (ImageView) findViewById(R.id.search_qr_btn);
        net_sch_layout = (OverScrollView) findViewById(R.id.net_sch_layout);

        //qr search
        search_qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrIntent = new Intent();
                qrIntent.setClass(mContext, CaptureActivity.class);
                startActivityForResult(qrIntent, REQUEST_QR);
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = search_edt.getText().toString();
                if (TextUtils.isEmpty(searchStr)) {
                    longToast("请输入搜索网校url");
                    return;
                }
                String url = "http://" + searchStr + Const.VERIFYSCHOOL;

                final LoadDialog loading = LoadDialog.create(mContext);
                loading.show();
                app.query.ajax(url, String.class, new AjaxCallback<String>(){
                    @Override
                    public void callback(String url, String object, AjaxStatus status) {
                        loading.dismiss();
                        int code = status.getCode();
                        if (code != Const.OK) {
                            PopupDialog.createNormal(mContext, "提示信息", "网络异常！请检查网络链接").show();
                            return;
                        }
                        try {
                            final SchoolResult result = app.gson.fromJson(
                                    object, new TypeToken<SchoolResult>() {
                            }.getType());

                            if (result == null) {
                                PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                                return;
                            }

                            StringBuilder message = new StringBuilder("正在进入...");
                            message.append(result.site.name);

                            PopupLoaingDialog.create(
                                    mContext,
                                    "搜索结果",
                                    message.toString(),
                                    new PopupLoaingDialog.PopupCallback() {
                                        @Override
                                        public void success() {
                                            app.setCurrentSchool(result.site);
                                            Intent courseIntent = new Intent(mContext,
                                                    SchCourseActivity.class);
                                            startActivity(courseIntent);
                                            finish();
                                        }
                                    }).show();
                        }catch (Exception e) {
                            PopupDialog.createNormal(mContext, "提示信息", "没有搜索到网校").show();
                        }
                    }
                });

            }
        });
     }

    private void showQrResultDlg(String result)
    {
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

    /**
     * 载入推荐网校信息列表
     */
    private void loadRecommendSchoolData()
    {
        workHandler.sendEmptyMessage(ProgressBarHandler.REFRESH_START);
        app.query.ajax(
                app.host + Const.RECOMMEND_SCHOOL, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object,
                                 AjaxStatus status) {
                ArrayList<RecommendSchoolItem> list = app.gson.fromJson(
                        object, new TypeToken<ArrayList<RecommendSchoolItem>>(){}.getType());
                load_layout.setVisibility(View.GONE);
                if (list == null) {
                    loadNoResultLayout();
                } else {
                    loadRecommendSchool(list);
                }
                workHandler.sendEmptyMessage(ProgressBarHandler.REFRESH_STOP);
            }
        });
    }

    private void loadNoResultLayout()
    {
        net_sch_layout.removeAllViews();
        View no_result_layout = getLayoutInflater().inflate(R.layout.no_result, null);
        net_sch_layout.addView(no_result_layout);
    }

    /**
     * 载入推荐网校
     * @param list
     */
    private void loadRecommendSchool(ArrayList<RecommendSchoolItem> list)
    {
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

    /**
     */
    private SearchView searchView;
    /**
     *
     * @param query
     */
    private void searchSchool(String query)
    {
        NetUtil.modelAjax(
                mContext, app.host + Const.SEARCH + query, new modelAjaxCallback<String>() {
            @Override
            public void callback(String result) {
                HashMap<String, String> resultMap = app.gson.fromJson(
                        result, new TypeToken<HashMap<String, String>>(){}.getType());
                if (resultMap != null) {
                    String status = resultMap.get("status");
                    if ("error".equals(status)) {
                        Toast.makeText(mContext, resultMap.get("message"), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    /**
     * 保存网校
     * @param item
     */
    private void saveRecommendSchool(RecommendSchoolItem item)
    {
        SharedPreferences sp = getSharedPreferences("recommend_school", MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(item.title, app.gson.toJson(item));
        edit.commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchSchool(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.normal_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
