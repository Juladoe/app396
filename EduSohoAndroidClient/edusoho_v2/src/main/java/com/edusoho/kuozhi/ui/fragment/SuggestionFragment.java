package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;

/**
 * Created by howzhi on 14-9-21.
 */
public class SuggestionFragment extends BaseFragment {

    private EditText mInfoEdt;
    private EditText mContactEdt;
    private RadioGroup mFixRadioGroup;
    private View mSubmitBtn;

    private static final String[] TYPES = {"bug", "fix"};

    @Override
    public String getTitle() {
        return "设置";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.suggestion_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mInfoEdt = (EditText) view.findViewById(R.id.suggestion_info_edt);
        mContactEdt = (EditText) view.findViewById(R.id.suggestion_contact_edt);
        mFixRadioGroup = (RadioGroup) view.findViewById(R.id.suggestion_fix_group);
        mSubmitBtn = view.findViewById(R.id.suggestion_submit);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info = mInfoEdt.getText().toString();
                if (TextUtils.isEmpty(info)) {
                    mActivity.longToast("请输入反馈内容!");
                    return;
                }

                String type = TYPES[getCheckIndex()];
                showProgress(true);
                mSubmitBtn.setEnabled(false);
                sendSuggesion(info, type, mContactEdt.getText().toString());
            }
        });
    }

    private int getCheckIndex() {
        int count = mFixRadioGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            RadioButton radioButton = (RadioButton) mFixRadioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                return i;
            }
        }
        return 0;
    }

    private void sendSuggesion(
            String info, String type, String contact) {
        RequestUrl requestUrl = app.bindUrl(Const.SUGGESTION, false);
        requestUrl.setParams(new String[]{
                "info", info,
                "type", type,
                "contact", contact
        });

        requestUrl.params.putAll(app.getPlatformInfo());

        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                showProgress(false);
                mSubmitBtn.setEnabled(true);
                mInfoEdt.setText("");
                mContactEdt.setText("");
                mActivity.longToast("提交成功！感谢反馈!");
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                showProgress(false);
                mSubmitBtn.setEnabled(true);
            }
        });
    }
}
