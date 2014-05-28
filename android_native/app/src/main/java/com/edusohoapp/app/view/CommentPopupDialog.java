package com.edusohoapp.app.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusohoapp.app.R;
import com.edusohoapp.app.entity.CourseCommentItem;
import com.edusohoapp.app.model.Review;
import com.edusohoapp.app.util.Const;

public class CommentPopupDialog extends Dialog{

    public static final int CANCEL = 0001;
    public static final int OK = 0002;

    private Context mContext;

    private RatingBar comment_rating;
    private EditText comment_input_edt;
    private PopupClickListener mClickListener;

	public CommentPopupDialog(Context context, int style, int theme, PopupClickListener clickListener) {
		super(context, theme);
		setContentView(style);
        mContext = context;
        initView();
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            addCommentBtn(getCurrentFocus().getWidth());
        }
    }

    private void initView()
    {
        comment_rating = (RatingBar) findViewById(R.id.comment_rating);
        comment_input_edt = (EditText) findViewById(R.id.comment_input_edt);

        findViewById(R.id.popup_ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(
                            OK,
                            comment_input_edt.getText().toString(),
                            comment_rating.getRating()
                    );
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
                        mClickListener.onClick(
                                CANCEL,
                                comment_input_edt.getText().toString(),
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
        comment_input_edt.setText(comment == null ? "" :comment.content);
        comment_rating.setRating(comment == null ? 0 : (float) comment.rating);
        show();
    }

    private void addCommentBtn(int width)
    {
        EduSohoAutoView qcl = (EduSohoAutoView) findViewById(R.id.quick_comment_layout);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        TextView view  = null;
        qcl.setParentWidth(width);

        View.OnClickListener quickClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = view.getTag(R.id.tag_first).toString();
                float rating = (Float)view.getTag(R.id.tag_second);
                mClickListener.quickComment(content, rating);
                dismiss();
            }
        };

        for (int i=0; i < Const.QUICK_COMMENTS.length; i++) {
            view = (TextView)inflater.inflate(R.layout.quick_comment_text, null);
            view.setText(Const.QUICK_COMMENTS[i].toString());
            view.setTag(R.id.tag_first, Const.QUICK_COMMENTS[i]);
            view.setTag(R.id.tag_second, Const.QUICK_COMMENTS[++i]);
            view.setOnClickListener(quickClickListener);
            qcl.addItem(view);
        }
    }


    public static CommentPopupDialog create(Context context)
    {
        CommentPopupDialog dlg = new CommentPopupDialog(
                context, R.layout.comment_popup, R.style.loadDlgTheme, null);
        return dlg;
    }

    public static interface PopupClickListener
    {
        public void onClick(int button, String message, float rating);
        public void quickComment(String message, float rating);
    }
}
