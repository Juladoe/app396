package com.howzhi;

import android.view.View;

import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.ui.course.CourseInfoActivity;
import com.edusoho.kuozhi.view.dialog.CommentPopupDialog;
import com.edusoho.listener.NormalCallback;

/**
 * Created by howzhi on 14-7-8.
 */
public class HowzhiCourseInfoActivity extends CourseInfoActivity {
    @Override
    public void showCommentDlg(Review loginUserComment, final View parent) {
        HowzhiCommentDlg dlg = HowzhiCommentDlg.create(mContext);
        dlg.showDlg(loginUserComment, new HowzhiCommentDlg.PopupClickListener() {
            @Override
            public void onClick(int button, String message, float rating) {
                switch (button) {
                    case CommentPopupDialog.OK:
                        addComment(message, rating, new NormalCallback() {
                            @Override
                            public void success(Object obj) {
                                hideEmptyLayout(parent);
                            }
                        });
                        break;
                }
            }
        });
    }
}
