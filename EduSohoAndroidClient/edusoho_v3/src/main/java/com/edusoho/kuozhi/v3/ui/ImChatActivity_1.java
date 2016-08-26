package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.edusoho.kuozhi.imserver.ui.MessageListFragment;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;

/**
 * Created by suju on 16/8/26.
 */
public class ImChatActivity_1 extends ActionBarBaseActivity {

    public static final String TAG = "ChatActivity";
    public static final String FROM_ID = "from_id";
    public static final String CONV_NO = "conv_no";
    public static final String MSG_DELIVERY = "msg_delivery";
    public static final String HEAD_IMAGE_URL = "head_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createView());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = Fragment.instantiate(mContext, MessageListFragment.class.getName());

        Bundle bundle = new Bundle();
        bundle.putString(MessageListFragment.CONV_NO, getIntent().getStringExtra(CONV_NO));
        bundle.putInt(MessageListFragment.CURRENT_ID, getAppSettingProvider().getCurrentUser().id);
        fragment.setArguments(bundle);
        fragmentTransaction.add(android.R.id.content, fragment,  "im_container").commit();
    }

    private View createView() {
        FrameLayout frameLayout = new FrameLayout(getBaseContext());
        frameLayout.setId(android.R.id.content);

        return frameLayout;
    }
}
