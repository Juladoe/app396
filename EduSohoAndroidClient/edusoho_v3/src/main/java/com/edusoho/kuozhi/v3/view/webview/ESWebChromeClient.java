package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.io.File;

/**
 * Created by howzhi on 15/8/10.
 */
public class ESWebChromeClient extends CordovaChromeClient {

    private Activity mActivity;

    public ESWebChromeClient(CordovaInterface cordova) {
        super(cordova);
        init();
    }

    public ESWebChromeClient(CordovaInterface ctx, CordovaWebView app) {
        super(ctx, app);
        init();
    }

    private void init() {
        mActivity = cordova.getActivity();
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        mActivity.setTitle(title);
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        this.openFileChooser(uploadMsg, "*/*");
    }

    @Override
    public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType ) {
        this.openFileChooser(uploadMsg, acceptType, null);
    }

    @Override
    public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture)
    {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(acceptType);
        cordova.startActivityForResult(
                new CordovaPlugin() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                        Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                        uploadMsg.onReceiveValue(compressImage(result));
                    }
                },
                Intent.createChooser(i, "File Browser"),
                FILECHOOSER_RESULTCODE);
    }

    private Uri compressImage(Uri uri) {
        if (uri == null || TextUtils.isEmpty(uri.getPath())) {
            return null;
        }
        String path = AppUtil.getPath(mActivity.getApplicationContext(), uri);
        Bitmap bitmap = AppUtil.getBitmapFromFile(new File(path));
        if (bitmap == null) {
            return uri;
        }
        bitmap = AppUtil.scaleImage(bitmap, EdusohoApp.screenW, 0);

        File cacheDir = AppUtil.getAppCacheDir();
        File newUriFile = new File(cacheDir, "uploadAvatarTemp.png");
        AppUtil.saveBitmap2FileWithQuality(bitmap, newUriFile.getAbsolutePath(), 80);

        return Uri.fromFile(newUriFile);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        PopupDialog.createNormal(mActivity, "提示:", message).show();
        result.cancel();
        return true;
    }
}
