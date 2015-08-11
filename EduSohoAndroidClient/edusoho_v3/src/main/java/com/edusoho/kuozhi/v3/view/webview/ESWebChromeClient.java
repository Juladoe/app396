package com.edusoho.kuozhi.v3.view.webview;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

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
                        uploadMsg.onReceiveValue(result);
                    }
                },
                Intent.createChooser(i, "File Browser"),
                FILECHOOSER_RESULTCODE);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        PopupDialog.createNormal(mActivity, "提示:", message).show();
        result.cancel();
        return true;
    }
}
