package com.edusoho.kowzhi.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Spinner;

import com.edusoho.kowzhi.R;

public class ExitCoursePopupDialog extends Dialog{

    private Spinner popup_select;
    private Context mContext;

    public static final int CANCEL = 0001;
    public static final int OK = 0002;

    private PopupClickListener mClickListener;

	public ExitCoursePopupDialog(Context context, int style, int theme, PopupClickListener clickListener) {
		super(context, theme);
		setContentView(style);
        mContext = context;
        mClickListener = clickListener;
        initView();
	}

    private void initView()
    {
        popup_select = (Spinner) findViewById(R.id.popup_select);
        findViewById(R.id.popup_ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    long selId = popup_select.getSelectedItemId();
                    String[] array = mContext.getResources().getStringArray(R.array.exitcourse_array);
                    mClickListener.onClick(OK, array[(int)selId]);
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
                        mClickListener.onClick(CANCEL, null);
                    }
                    dismiss();
                }
            });
        }

    }

    public static ExitCoursePopupDialog create(
            Context context,PopupClickListener clickListener)
    {
        ExitCoursePopupDialog dlg = new ExitCoursePopupDialog(
                context, R.layout.popup_select, R.style.loadDlgTheme, clickListener);

        return dlg;
    }

    public static interface PopupClickListener
    {
        public void onClick(int button, String selStr);
    }
}
