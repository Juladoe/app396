package com.edusoho.kuozhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.edusoho.kuozhi.R;

/**
 * Created by howzhi on 14-8-7.
 */
public class EdusohoMaterialDialog extends Dialog {

    private TextView popTitle;
    private TextView popMessage;

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

    public static interface PopupClickListener
    {
        public void onClick(int button);
    }
}
