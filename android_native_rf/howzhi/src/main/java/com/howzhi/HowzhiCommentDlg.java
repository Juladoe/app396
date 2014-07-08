package com.howzhi;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import com.edusoho.kuozhi.model.Review;

/**
 * Created by howzhi on 14-7-8.
 */
public class HowzhiCommentDlg extends Dialog {
    public static final int CANCEL = 0001;
    public static final int OK = 0002;

    private Context mContext;

    private RatingBar comment_rating;
    private PopupClickListener mClickListener;

    public HowzhiCommentDlg(Context context, int style, int theme, PopupClickListener clickListener) {
        super(context, theme);
        setContentView(style);
        mContext = context;
        initView();
    }

    private void initView()
    {
        comment_rating = (RatingBar) findViewById(com.edusoho.kuozhi.R.id.comment_rating);

        findViewById(com.edusoho.kuozhi.R.id.popup_ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(
                            OK,
                            "",
                            comment_rating.getRating()
                    );
                }
                dismiss();
            }
        });

        View cancelBtn =  findViewById(com.edusoho.kuozhi.R.id.popup_cancel_btn);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        mClickListener.onClick(
                                CANCEL,
                                "",
                                comment_rating.getRating()
                        );
                    }
                    dismiss();
                }
            });
        }
    }

    public void showDlg(Review comment, PopupClickListener listener)
    {
        mClickListener = listener;
        comment_rating.setRating(comment == null ? 0 : (float) comment.rating);
        show();
    }

    public static HowzhiCommentDlg create(Context context)
    {
        HowzhiCommentDlg dlg = new HowzhiCommentDlg(
                context, R.layout.howzhi_comment_popup, com.edusoho.kuozhi.R.style.loadDlgTheme, null);
        return dlg;
    }

    public static interface PopupClickListener
    {
        public void onClick(int button, String message, float rating);
    }
}
