package com.edusoho.kuozhi.shard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static cn.sharesdk.framework.utils.R.getBitmapRes;

public class MainActivity extends Activity {
    private Context mContext;
    private OnekeyShare mOneKeyShare;
    private ShareParams mShareParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        //添加应用信息
        mShareParams = new ShareParams(this);
        mShareParams.initShareParams(
                R.drawable.ic_launcher,
                getString(R.string.app_name),
                "分享",
                "http://sharesdk.cn",
                "我是分享文本",
                "/sdcard/test.jpg",
                getString(R.string.app_name)
        );


        mShareParams.show(new ShareHandler() {
            @Override
            public void handler(String type) {
                Log.d(null, "ytpe");
            }
        });
        //init();
    }

    public void init(){
        Platform[] platforms = ShareSDK.getPlatformList();
        List<ListData> list = new ArrayList<ListData>();
        for (int i=0;i<platforms.length;i++){
            String name = platforms[i].getName();
            String resName = "logo_" + name;
            int resId = getBitmapRes(this, resName);
//            ListData data = new ListData(getResources().getDrawable(resId),name);
//            list.add(data);
        }
        mContext = this;
        ListView listView = new ListView(mContext);
        ShardListAdapter adapter = new ShardListAdapter(mContext, list, R.layout.shard_list_item);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alertDialog = builder
                .setTitle("分享课程")
                .setView(listView)
                .create();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListData data = (ListData) parent.getItemAtPosition(position);
                mOneKeyShare.setPlatform(data.text);
                mOneKeyShare.setSilent(false);
//                mShareParams.shareShow();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
