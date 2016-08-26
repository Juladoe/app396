package com.edusoho.kuozhi.imserver.ui.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.imserver.R;
import com.edusoho.kuozhi.imserver.ui.listener.MessageControllerListener;
import com.edusoho.kuozhi.imserver.ui.listener.MessageSendListener;
import com.edusoho.kuozhi.imserver.util.SystemUtil;

/**
 * Created by suju on 16/8/26.
 */
public class MessageInputView extends FrameLayout {

    protected ESIconView btnVoice;
    protected ESIconView btnKeyBoard;
    protected EditText etSend;
    protected Button btnSend;
    protected ESIconView ivAddMedia;
    protected View viewMediaLayout;
    protected View viewPressToSpeak;
    protected View viewMsgInput;
    protected TextView tvSpeak;
    protected TextView tvSpeakHint;
    protected View mViewSpeakContainer;
    protected ImageView ivRecordImage;

    private MessageSendListener mMessageSendListener;
    private MessageControllerListener mMessageControllerListener;

    public MessageInputView(Context context) {
        super(context);
        createView();
    }

    public MessageInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    private void createView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_message_list_tool_layout, this, true);
        initView();
    }

    public void setMessageSendListener(MessageSendListener listener) {
        this.mMessageSendListener = listener;
    }

    public void setMessageControllerListener(MessageControllerListener listener) {
        this.mMessageControllerListener = listener;
    }

    protected void initView() {
        OnClickListener onClickListener = getViewOnClickListener();
        etSend = (EditText) findViewById(R.id.et_send_content);
        etSend.addTextChangedListener(mContentTextWatcher);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(onClickListener);
        etSend.setOnFocusChangeListener(getContentOnFocusChangeListener());
        etSend.setOnClickListener(onClickListener);
        ivAddMedia = (ESIconView) findViewById(R.id.iv_show_media_layout);
        ivAddMedia.setOnClickListener(onClickListener);
        viewMediaLayout = findViewById(R.id.ll_media_layout);
        btnVoice = (ESIconView) findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(onClickListener);
        btnKeyBoard = (ESIconView) findViewById(R.id.btn_set_mode_keyboard);
        btnKeyBoard.setOnClickListener(onClickListener);
        viewPressToSpeak = findViewById(R.id.rl_btn_press_to_speak);
        viewPressToSpeak.setOnTouchListener(getViewOnTouchListener());
        viewPressToSpeak.setOnClickListener(onClickListener);
        viewMsgInput = findViewById(R.id.rl_msg_input);

        ESIconView ivPhoto = (ESIconView) findViewById(R.id.iv_image);
        ivPhoto.setOnClickListener(onClickListener);
        ESIconView ivCamera = (ESIconView) findViewById(R.id.iv_camera);
        ivCamera.setOnClickListener(onClickListener);
        tvSpeak = (TextView) findViewById(R.id.tv_speak);
        tvSpeakHint = (TextView) findViewById(R.id.tv_speak_hint);
        ivRecordImage = (ImageView) findViewById(R.id.iv_voice_volume);
        //mViewSpeakContainer = findViewById(R.id.recording_container);
        //mViewSpeakContainer.bringToFront();
    }

    protected OnFocusChangeListener getContentOnFocusChangeListener() {
        return new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    viewMediaLayout.setVisibility(View.GONE);
                    SystemUtil.setSoftKeyBoard(etSend, getContext(), SystemUtil.SHOW_KEYBOARD);
                    //lvMessage.post(mListViewSelectRunnable);
                }
            }
        };
    }

    protected OnClickListener getViewOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.et_send_content) {
                    //lvMessage.post(mListViewSelectRunnable);
                } else if (v.getId() == R.id.iv_show_media_layout) {
                    //加号，显示多媒体框
                    if (viewMediaLayout.getVisibility() == View.GONE) {
                        viewMediaLayout.setVisibility(View.VISIBLE);
                        etSend.clearFocus();
                        SystemUtil.setSoftKeyBoard(etSend, getContext(), SystemUtil.HIDE_KEYBOARD);
                    } else {
                        viewMediaLayout.setVisibility(View.GONE);
                    }
                    //lvMessage.post(mListViewSelectRunnable);
                } else if (v.getId() == R.id.btn_send) {
                    //发送消息
                    if (etSend.getText().length() == 0) {
                        return;
                    }
                    mMessageSendListener.onSendMessage(etSend.getText().toString());
                } else if (v.getId() == R.id.btn_voice) {
                    //语音
                    viewMediaLayout.setVisibility(View.GONE);
                    btnVoice.setVisibility(View.GONE);
                    viewMsgInput.setVisibility(View.GONE);
                    btnKeyBoard.setVisibility(View.VISIBLE);
                    viewPressToSpeak.setVisibility(View.VISIBLE);
                    SystemUtil.setSoftKeyBoard(etSend, getContext(), SystemUtil.HIDE_KEYBOARD);
                } else if (v.getId() == R.id.btn_set_mode_keyboard) {
                    //键盘
                    viewMediaLayout.setVisibility(View.GONE);
                    btnVoice.setVisibility(View.VISIBLE);
                    viewPressToSpeak.setVisibility(View.GONE);
                    viewMsgInput.setVisibility(View.VISIBLE);
                    btnKeyBoard.setVisibility(View.GONE);
                    etSend.requestFocus();
                    //lvMessage.post(mListViewSelectRunnable);
                } else if (v.getId() == R.id.rl_btn_press_to_speak) {
                    viewMediaLayout.setVisibility(View.GONE);
                } else if (v.getId() == R.id.iv_image) {
                    //openPictureFromLocal();
                    mMessageControllerListener.onSelectPhoto();
                } else if (v.getId() == R.id.iv_camera) {
                    //openPictureFromCamera();
                    mMessageControllerListener.onTakePhoto();
                }
            }
        };
    }

    protected OnTouchListener getViewOnTouchListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        };
    }

    protected TextWatcher mContentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s)) {
                btnSend.setVisibility(View.VISIBLE);
                ivAddMedia.setVisibility(View.GONE);
            } else {
                ivAddMedia.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
