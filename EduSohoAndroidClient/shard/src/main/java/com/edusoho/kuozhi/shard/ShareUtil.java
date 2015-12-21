package com.edusoho.kuozhi.shard;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import static com.mob.tools.utils.R.getBitmapRes;


/**
 * Created by onewoman on 14-11-6.
 */
public class ShareUtil {
    public int mNotification_icon;
    public String mNotification_text;
    public String mShareTextTitle;
    public String mShareTitleUrl;
    public String mShareText;
    public String mLocalImagePath;
    public String mShareSite;
    private OnekeyShare mOneKeyShare;
    private Context mContext;
    private ShardDialog mAlertDialog;
    private ShareHandler mShareHandler;
    private static ShareUtil shareUtil;

    private List<ListData> mCustomList;

    private ShareUtil(Context context) {
        //添加应用信息
        new ShareSDKUtil().initSDK(context);
        mContext = context;
    }

    public static ShareUtil getShareUtil(Context context) {
        return new ShareUtil(context);
    }

    public void setCustomList(List<ListData> dataList) {
        this.mCustomList = dataList;
    }

    public ShareUtil initShareParams(
            int icon, String shareTextTitle, String shareTitleUrl, String shareText, File imageFile, String ShareSite
    ) {
        mOneKeyShare = new OnekeyShare();
        mNotification_icon = icon;
        mShareTextTitle = shareTextTitle;
        mShareTitleUrl = shareTitleUrl;
        mShareText = shareText;
        if (imageFile.exists()) {
            mLocalImagePath = imageFile.getAbsolutePath();
            mOneKeyShare.setImagePath(mLocalImagePath);
        } else {
        }

        mShareSite = ShareSite;
        initOneKeyShare();
        initDialog();
        return this;
    }

    private boolean filterPlat(String name) {
        String[] filters = mContext.getResources().getStringArray(R.array.shard_filter);
        for (String filter : filters) {
            if (filter.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public Platform getPlatForm(String name) {
        return ShareSDK.getPlatform(name);
    }

    public void initDialog() {
        Platform[] platforms = ShareSDK.getPlatformList();
        ArrayList<ListData> list = new ArrayList<ListData>();

        for (Platform platform : platforms) {
            String name = platform.getName();
            if (filterPlat(name)) {
                continue;
            }
            String resName = "logo_" + name;
            int resId = getBitmapRes(mContext, resName);
            ListData data = new ListData(mContext.getResources().getDrawable(resId), name, mContext);
            list.add(data);
        }

        Collections.sort(list, new Comparator<ListData>() {
            @Override
            public int compare(ListData lhs, ListData rhs) {
                return rhs.type.compareToIgnoreCase(lhs.type);
            }
        });

        if (mCustomList != null) {
            list.addAll(mCustomList);
        }

        mAlertDialog = new ShardDialog(mContext);
        mAlertDialog.setShardDatas(list);
        mAlertDialog.setShardItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListData data = (ListData) parent.getItemAtPosition(position);

                if (mShareHandler != null && mShareHandler.handler(data.type)) {
                    mAlertDialog.dismiss();
                    return;
                }

                mOneKeyShare.setPlatform(data.type);
                mOneKeyShare.setSilent(false);
                mOneKeyShare.show(mContext);
                mAlertDialog.dismiss();
            }
        });
    }

    public void show(ShareHandler shareHandler) {
        this.mShareHandler = shareHandler;
        mAlertDialog.show();
    }

    private void initOneKeyShare() {
        //关闭sso授权
        mOneKeyShare.disableSSOWhenAuthorize();
        mOneKeyShare.setDialogMode();
        // 分享时Notification的图标和文字
        //mOneKeyShare.setNotification(mNotification_icon, mNotification_text);
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        mOneKeyShare.setTitle(mShareTextTitle);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        mOneKeyShare.setTitleUrl(mShareTitleUrl);
        // text是分享文本，所有平台都需要这个字段
        mOneKeyShare.setText(mShareText);
//        imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        mOneKeyShare.setImagePath(mLocalImagePath);
//        imageUrl是图片的网络路径，新浪微博、人人网、QQ空间、
//        微信的两个平台、Linked-In支持此字段
//        mOneKeyShare.setImageUrl(mLocalImagePath);
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://sharesdk.cn");
        // site是分享此内容的网站名称，仅在QQ空间使用
        mOneKeyShare.setSite(mShareSite);
    }

}
