package com.edusoho.kowzhi.view.plugin;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edusoho.kowzhi.R;

import java.util.Timer;
import java.util.TimerTask;

public class PopupLoaingDialog extends Dialog{

    private TextView popTitle;
    private TextView popMessage;
    private ProgressBar loading_progress;

    public static final int CANCEL = 0001;
    public static final int OK = 0002;

    private PopupCallback mClickListener;

	public PopupLoaingDialog(Context context, int style, int theme, PopupCallback clickListener) {
		super(context, theme);
		setContentView(style);
        mClickListener = clickListener;
        initView();
	}

    private void initView()
    {
        popTitle = (TextView) findViewById(R.id.popup_title);
        popMessage = (TextView) findViewById(R.id.popup_message);

        View cancelBtn =  findViewById(R.id.popup_cancel_btn);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    workTimer.cancel();
                    workTimer.purge();
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

    public static PopupLoaingDialog create(
            Context context, String title, String msg, PopupCallback clickListener)
    {
        PopupLoaingDialog dlg = new PopupLoaingDialog(
                context, R.layout.popup_loading, R.style.loadDlgTheme, clickListener);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.startTimeWrok();
        return dlg;
    }

    private Timer workTimer;

    private void startTimeWrok()
    {
        workTimer = new Timer();

        TimerTask endTask = new TimerTask() {
            @Override
            public void run() {
                mClickListener.success();
                workTimer.cancel();
                workTimer.purge();
            }
        };
        workTimer.schedule(endTask, 2000);
    }

    public static interface PopupCallback
    {
        public void success();
    }
}
