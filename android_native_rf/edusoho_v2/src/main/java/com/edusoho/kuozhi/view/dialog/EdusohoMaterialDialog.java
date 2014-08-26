package com.edusoho.kuozhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-8-7.
 */
public class EdusohoMaterialDialog extends Dialog {

    private TextView popTitle;
    private TextView popMessage;
    private ViewStub mContentViewStub;

    public static final int CANCEL = 0001;
    public static final int OK = 0002;

    private PopupClickListener mClickListener;

    public EdusohoMaterialDialog(Context context, int style, int theme, PopupClickListener clickListener) {
        super(context, theme);
        setContentView(style);
        mClickListener = clickListener;
        initView();
    }

    private TextView mPopupOkBtn;
    private TextView mPopupCancelBtn;

    private void initView()
    {
        mContentViewStub = (ViewStub) findViewById(R.id.material_dlg_viewstub);
        popTitle = (TextView) findViewById(R.id.material_title);
        popMessage = (TextView) findViewById(R.id.material_content);
        mPopupOkBtn = (TextView) findViewById(R.id.material_ok_btn);

        mPopupOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(OK);
                }
                dismiss();
            }
        });
        mPopupCancelBtn = (TextView) findViewById(R.id.material_cancel_btn);
        mPopupCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(CANCEL);
                }
                dismiss();
            }
        });
    }

    private void hideCancelBtn()
    {
        mPopupCancelBtn.setVisibility(View.GONE);
    }

    public void setTitle(String title)
    {
        popTitle.setText(title);
    }

    public void setMessage(String message)
    {
        popMessage.setText(message);
    }

    public void setMessage(Spanned message)
    {
        popMessage.setText(message);
    }

    public static EdusohoMaterialDialog createMuilt(
            Context context, String title, String msg, PopupClickListener clickListener)
    {
        EdusohoMaterialDialog dlg = new EdusohoMaterialDialog(
                context, R.layout.material_dlg, R.style.loadDlgTheme, clickListener);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        return dlg;
    }

    public void setCancelText(String text)
    {
        mPopupCancelBtn.setText(text);
    }

    public void setOkText(String text)
    {
        mPopupOkBtn.setText(text);
    }

    public static EdusohoMaterialDialog createNormal(Context context, String title, String msg)
    {
        EdusohoMaterialDialog dlg = new EdusohoMaterialDialog(
                context, R.layout.material_dlg, R.style.loadDlgTheme, null);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.hideCancelBtn();
        return dlg;
    }

    public static EdusohoMaterialDialog createNormalFromUrl(Context context, String title, String url)
    {
        EdusohoMaterialDialog dlg = new EdusohoMaterialDialog(
                context, R.layout.material_dlg, R.style.loadDlgTheme, null);
        dlg.setTitle(title);
        dlg.hideCancelBtn();
        dlg.showMessageOnWebview(url);
        return dlg;
    }

    private void showMessageOnWebview(final String url)
    {
        mContentViewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                WebView webView = (WebView)view.findViewById(R.id.dlg_content);
                webView.loadUrl(url);
            }
        });

        mContentViewStub.inflate();
    }

    public static interface PopupClickListener
    {
        public void onClick(int button);
    }
}
