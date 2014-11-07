package com.edusoho.kuozhi.shard;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static cn.sharesdk.framework.utils.R.getBitmapRes;

/**
 * Created by onewoman on 14-11-6.
 */
public class ShareParams {
    public int mNotification_icon;
    public String mNotification_text;
    public String mShareTextTitle;
    public String mShareTitleUrl;
    public String mShareText;
    public String mLocalImagePath;
    public String mShareSite;
    private OnekeyShare mOneKeyShare;
    private Context mContext;
    private AlertDialog mAlertDialog;
    private ShareHandler mShareHandler;

    public ShareParams(Context context){
        //添加应用信息
        ShareSDK.initSDK(context);
        mOneKeyShare = new OnekeyShare();
        mContext = context;
        initDialog();
    }

    public ShareParams initShareParams(
            String shareTextTitle,String shareTitleUrl, String shareText, String localImagePath, String ShareSite
    )
    {
        mShareTextTitle = shareTextTitle;
        mShareTitleUrl = shareTitleUrl;
        mShareText = shareText;
        mLocalImagePath = localImagePath;
        mShareSite = ShareSite;

        initOneKeyShare();
        return this;
    }

    public void initDialog(){
        Platform[] platforms = ShareSDK.getPlatformList();
        List<ListData> list = new ArrayList<ListData>();
        for (int i=0;i<platforms.length;i++){
            String name = platforms[i].getName();
            String resName = "logo_" + name;
            int resId = getBitmapRes(mContext, resName);
            ListData data = new ListData(mContext.getResources().getDrawable(resId),name,mContext);
            list.add(data);
        }
        ListView listView = new ListView(mContext);
        ShardListAdapter adapter = new ShardListAdapter(mContext, list, R.layout.shard_list_item);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mAlertDialog = builder
                .setTitle("分享课程")
                .setView(listView)
                .create();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListData data = (ListData) parent.getItemAtPosition(position);
                if (data.type.startsWith("Wechat")) {
                    if (mShareHandler != null) {
                        mShareHandler.handler(data.type);
                        mAlertDialog.dismiss();
                    }
                    return;
                }
                mOneKeyShare.setPlatform(data.type);
                mOneKeyShare.setSilent(false);
                mOneKeyShare.show(mContext);
                mAlertDialog.dismiss();
            }
        });
    }

    public void show(ShareHandler shareHandler)
    {
        this.mShareHandler = shareHandler;
        mAlertDialog.show();
    }

    private void initOneKeyShare() {
        //关闭sso授权
        mOneKeyShare.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字
        mOneKeyShare.setNotification(R.drawable.app_splash, mNotification_text);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        mOneKeyShare.setTitle("分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        mOneKeyShare.setTitleUrl(mShareTitleUrl);
        // text是分享文本，所有平台都需要这个字段
        mOneKeyShare.setText(mShareText);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        mOneKeyShare.setImagePath(mLocalImagePath);
        // imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
        // 微信的两个平台、Linked-In支持此字段
//        oks.setImageUrl("http://www.krbb.cn/yefiles/images/20111013/20111013gkxzejgmyr.JPG");
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
        // site是分享此内容的网站名称，仅在QQ空间使用
        mOneKeyShare.setSite(mShareSite);
    }

}
