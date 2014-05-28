package com.edusohoapp.app.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.edusohoapp.app.R;

public class PopupDialog extends Dialog{

    private TextView popTitle;
    private TextView popMessage;

    public static final int CANCEL = 0001;
    public static final int OK = 0002;

    private PopupClickListener mClickListener;

	public PopupDialog(Context context, int style, int theme, PopupClickListener clickListener) {
		super(context, theme);
		setContentView(style);
        mClickListener = clickListener;
        initView();
	}

    private void initView()
    {
        popTitle = (TextView) findViewById(R.id.popup_title);
        popMessage = (TextView) findViewById(R.id.popup_message);
        findViewById(R.id.popup_ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(OK);
                }
                dismiss();
            }
        });
        View cancelBtn =  findViewById(R.id.popup_cancel_btn);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onClick(CANCEL);
                    }
                    dismiss();
                }
            });
        }

    }

    public void setTitle(String title)
    {
        popTitle.setText(title);
    }

    public void setMessage(String message)
    {
        popMessage.setText(message);
    }

	public static PopupDialog createMuilt(
            Context context, String title, String msg, PopupClickListener clickListener)
	{
        PopupDialog dlg = new PopupDialog(
                context, R.layout.popup_muilt, R.style.loadDlgTheme, clickListener);
        dlg.setTitle(title);
        dlg.setMessage(msg);
		return dlg;
	}

    public static PopupDialog createNormal(Context context, String title, String msg)
    {
        PopupDialog dlg = new PopupDialog(
                context, R.layout.popup, R.style.loadDlgTheme, null);
        dlg.setTitle(title);
        dlg.setMessage(msg);

        return dlg;
    }

    public static interface PopupClickListener
    {
        public void onClick(int button);
    }
}
