package com.edusoho.kuozhi.v3.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ShareHandler;
import com.edusoho.kuozhi.shard.ShareUtil;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;

/**
 * Created by JesseHuang on 15/6/19.
 */
public class ShareTool {
    private Context mContext;
    private String mUrl = "";
    private String mTitle = "";
    private String mAbout = "";
    private String mPic = "";

    public ShareTool(Context ctx, String url, String title, String about, String pic) {
        mContext = ctx;
        mUrl = url;
        mTitle = title;
        mAbout = about;
        mPic = pic;
    }

    public void shardCourse() {
        final ShareUtil shareUtil = ShareUtil.getShareUtil(mContext);
        ImageLoader.getInstance().loadImage(mPic, EdusohoApp.app.mOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                CommonUtil.longToast(mContext, "课程图片获取失败");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                File file = ImageLoader.getInstance().getDiskCache().get(imageUri);
                shareUtil.initShareParams(
                        R.mipmap.ic_launcher,
                        mTitle,
                        mUrl,
                        AppUtil.coverCourseAbout(mAbout),
                        file,
                        EdusohoApp.app.host
                );
                shareUtil.show(new ShareHandler() {
                    @Override
                    public void handler(String type) {
                        //朋友圈
                        int wxType = SendMessageToWX.Req.WXSceneTimeline;
                        if ("Wechat".equals(type)) {
                            wxType = SendMessageToWX.Req.WXSceneSession;
                        }
                        shardToMM(mContext, wxType);
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private boolean shardToMM(Context context, int type) {
        String APP_ID = mContext.getResources().getString(R.string.app_id);
        IWXAPI wxApi;
        wxApi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        wxApi.registerApp(APP_ID);
        WXTextObject wXTextObject = new WXTextObject();
        wXTextObject.text = "分享课程";
        WXWebpageObject wxobj = new WXWebpageObject();

        wxobj.webpageUrl = mUrl;
        WXMediaMessage wXMediaMessage = new WXMediaMessage();
        wXMediaMessage.mediaObject = wxobj;
        wXMediaMessage.description = AppUtil.coverCourseAbout(mAbout);
        wXMediaMessage.title = mTitle;
        //具体尺寸new ImageSize(100, 99)待修改
        wXMediaMessage.setThumbImage(ImageLoader.getInstance().loadImageSync(mPic,
                new ImageSize(100, 99), EdusohoApp.app.mOptions));

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = type;
        req.transaction = System.currentTimeMillis() + "";
        req.message = wXMediaMessage;
        return wxApi.sendReq(req);
    }

}
