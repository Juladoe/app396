package com.edusoho.kuozhi.v3.ui.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DefaultPageActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoCompoundButton;
import com.edusoho.kuozhi.v3.view.dialog.PopupDialog;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayDeque;
import java.util.Queue;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JesseHuang on 15/4/23.
 * 用于包含ActionBar的theme
 */
public class ActionBarBaseActivity extends BaseActivity implements MessageEngine.MessageCallback {

    public static final String TAG = "ActionBarBaseActivity";
    public static final String BACK = "返回";
    public ActionBar mActionBar;
    protected TextView mTitleTextView;
    private View mTitleLayoutView;
    private View mTitleLoading;
    protected int mRunStatus;
    private EduSohoCompoundButton switchButton;
    private RadioButton rbStudyRadioButton;
    private RadioButton rbDiscussRadioButton;
    private CircleImageView civBadgeView;
    private Queue<WidgetMessage> mUIMessageQueue;

    protected XGPushClickedResult mXGClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        app.registMsgSource(this);
        mUIMessageQueue = new ArrayDeque<>();
        if (mActionBar != null) {
            mActionBar.setWindowTitle("title");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunStatus = MSG_RESUME;
        MobclickAgent.onResume(mContext);
        mXGClick = XGPushManager.onActivityStarted(this);
        Log.d("TPush", "onResumeXGPushClickedResult:" + mXGClick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRunStatus = MSG_PAUSE;
        MobclickAgent.onPause(mContext);
        Log.d("MainActivity-->", "onPause");
        XGPushManager.onActivityStoped(this);
        mXGClick = null;
    }

    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
    }

    @Override
    public void setTitle(CharSequence title) {
        setBackMode(BACK, title.toString());
    }

    public void setBackMode(String backTitle, String title) {
        mTitleLayoutView = getLayoutInflater().inflate(R.layout.actionbar_custom_title, null);
        mTitleTextView = (TextView) mTitleLayoutView.findViewById(R.id.tv_action_bar_title);
        mTitleTextView.setText(title);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        mActionBar.setCustomView(mTitleLayoutView, layoutParams);

        if (backTitle != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void initSwitchButton(String backTitle, String roleTitle, RadioGroup.OnCheckedChangeListener clickListener) {
        if (backTitle != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (clickListener != null) {
            View switchButtonLayout = getLayoutInflater().inflate(R.layout.actionbar_course_switch_button, null);
            switchButton = (EduSohoCompoundButton) switchButtonLayout.findViewById(R.id.ecb_switch);
            rbStudyRadioButton = (RadioButton) switchButtonLayout.findViewById(R.id.rb_study);
            rbDiscussRadioButton = (RadioButton) switchButtonLayout.findViewById(R.id.rb_discuss);
            civBadgeView = (CircleImageView) switchButtonLayout.findViewById(R.id.civ_badge_view);
            rbStudyRadioButton.setText(roleTitle);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            mActionBar.setCustomView(switchButtonLayout, layoutParams);
            switchButton.setOnCheckedChangeListener(clickListener);
        }
    }

    public void setSwitchBadgeViewVisible(int visible) {
        if (civBadgeView != null) {
            civBadgeView.setVisibility(visible);
        }
    }

    public void setRadioButtonChecked(int id) {
        if (rbStudyRadioButton.getId() == id) {
            rbStudyRadioButton.setChecked(true);
        } else {
            rbDiscussRadioButton.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.unRegistMsgSource(this);
        mUIMessageQueue.clear();
    }

    protected void invokeUIMessage() {
        WidgetMessage message;
        while ((message = mUIMessageQueue.poll()) != null) {
            invoke(message);
        }
    }

    protected void saveMessage(WidgetMessage message) {
        mUIMessageQueue.add(message);
    }

    @Override
    public void invoke(WidgetMessage message) {
    }

    protected int getRunStatus() {
        return mRunStatus;
    }

    protected void processMessage(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.TOKEN_LOSE.equals(messageType.type)) {
            PopupDialog dialog = PopupDialog.createNormal(mActivity, "提示", getString(R.string.token_lose_notice));
            dialog.setOkListener(new PopupDialog.PopupClickListener() {
                @Override
                public void onClick(int button) {
                    handleTokenLostMsg();
                    finish();
                }
            });
            dialog.show();
        }
    }

    protected void handleTokenLostMsg() {
        Bundle bundle = new Bundle();
        bundle.putString(Const.BIND_USER_ID, "");
        app.pushUnregister(bundle);
        app.removeToken();
        MessageEngine.getInstance().sendMsg(Const.LOGOUT_SUCCESS, null);
        MessageEngine.getInstance().sendMsgToTaget(Const.SWITCH_TAB, null, DefaultPageActivity.class);
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[0];
    }

    @Override
    public int getMode() {
        return REGIST_CLASS;
    }
}
