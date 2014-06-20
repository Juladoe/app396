package com.edusoho.kuozhi.view.dialog;

import com.edusoho.kuozhi.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class LoadDialog extends Dialog{

    private TextView loading_txt;

	public LoadDialog(Context context) {
		super(context);
		setContentView(R.layout.load_dig_layout);
        initView();
	}

    private void initView() {
        loading_txt = (TextView) findViewById(R.id.loading_txt);
    }

    public LoadDialog(Context context, int theme) {
		super(context, theme);
		setContentView(R.layout.load_dig_layout);
        initView();
	}
	
	public static LoadDialog create(Context context)
	{
		return new LoadDialog(context, R.style.loadDlgTheme);
	}

    public void showAutoHide(String message)
    {
        loading_txt.setText(message);
        show();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LoadDialog.this.dismiss();
            }
        }, 2000);
    }
}
