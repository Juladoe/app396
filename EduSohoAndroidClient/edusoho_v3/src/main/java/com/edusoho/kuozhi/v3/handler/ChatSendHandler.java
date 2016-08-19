package com.edusoho.kuozhi.v3.handler;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.bal.push.RedirectBody;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.edusoho.kuozhi.v3.view.dialog.RedirectPreViewDialog;


/**
 * Created by howzhi on 15/9/30.
 */
public class ChatSendHandler {

    public static final int REQUEST_SELECT_FRIEND = 0010;
    public static final int RESULT_SELECT_FRIEND_OK = 0020;

    //must Activity
    protected int mItemIndex;
    protected BaseActivity mActivity;
    protected RedirectBody mRedirectBody;

    public ChatSendHandler(BaseActivity activity, RedirectBody redirectBody, int itemIndex) {
        mActivity = activity;
        mItemIndex = itemIndex;
        mRedirectBody = redirectBody;
    }

    public void handleClick(final NormalCallback finishCallback) {
        RedirectPreViewDialog dialog = RedirectPreViewDialog.getBuilder(mActivity)
                .setLayout(R.layout.redirect_preview_layout)
                .setTitle(mRedirectBody.title)
                .setBody(mRedirectBody.content)
                .setIconByUri(mRedirectBody.image)
                .build();
        dialog.show();
        dialog.setButtonClickListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                if (button == PopupDialog.OK) {
                    finishCallback.success(mItemIndex);
                }
            }
        });
    }

}
