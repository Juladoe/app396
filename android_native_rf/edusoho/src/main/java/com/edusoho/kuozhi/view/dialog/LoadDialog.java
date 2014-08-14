package com.edusoho.kuozhi.view.dialog;

import com.edusoho.kuozhi.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class LoadDialog extends Dialog{

    private TextView loading_txt;
    private LoadingCompleCallback mLoadingCompleCallback;
    private static final int DEFAULT_LOAD_TIME = 2000;
    private int mAutoLoadTime;

	public LoadDialog(Context context) {
		super(context);
		setContentView(R.layout.load_dig_layout);
        initView();
	}

    public LoadDialog(Context context, int theme, LoadingCompleCallback loadingCompleCallback) {
        super(context, theme);
        setContentView(R.layout.load_dig_layout);
        mLoadingCompleCallback = loadingCompleCallback;
        initView();
    }

    private void initView() {
        mAutoLoadTime = DEFAULT_LOAD_TIME;
        loading_txt = (TextView) findViewById(R.id.loading_txt);
        setCanceledOnTouchOutside(false);
    }

    public LoadDialog(Context context, int theme) {
		super(context, theme);
		setContentView(R.layout.load_dig_layout);
        initView();
	}

    public static LoadDialog create(Context context, LoadingCompleCallback loadingCompleCallback)
    {
        return new LoadDialog(context, R.style.loadDlgTheme, loadingCompleCallback);
    }

	public static LoadDialog create(Context context)
	{
		return new LoadDialog(context, R.style.loadDlgTheme);
	}

    public void setAutoLoadTime(int time)
    {
        this.mAutoLoadTime = time;
    }

    public void showAutoHide(String message)
    {
        loading_txt.setText(message);
        show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LoadDialog.this.dismiss();
                if (mLoadingCompleCallback != null) {
                    mLoadingCompleCallback.success();
                }
            }
        }, mAutoLoadTime);
    }

    public static interface LoadingCompleCallback
    {
        public void success();
    }
}
