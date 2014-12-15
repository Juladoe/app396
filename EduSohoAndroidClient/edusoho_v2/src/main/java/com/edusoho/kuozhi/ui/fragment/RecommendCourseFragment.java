package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoButton;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * Created by howzhi on 14-9-19.
 */
public class RecommendCourseFragment extends BaseFragment {

    private RatingBar courseRatingBar;
    private EditText courseInput;
    private ActionBarBaseActivity mActivity;
    private EdusohoButton mCommitBtn;
    private int mCourseId;

    @Override
    public String getTitle() {
        return "评价";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ActionBarBaseActivity) getActivity();
        setContainerView(R.layout.recomend_course_fragment);
        //setStyle(DialogFragment.STYLE_NO_TITLE, R.style.PopDialogTheme);
        if (app.loginUser == null) {
            LoginActivity.start(mActivity);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCourseId = bundle.getInt(Const.COURSE_ID);
        }
    }

    private void bindViewListener()
    {
        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(courseInput.getText())) {
                    mActivity.longToast("请输入评论内容!");
                    return;
                }
                mCommitBtn.setStatus(EdusohoButton.PROGRESS);
                RequestUrl requestUrl = mActivity.app.bindUrl(Const.ADDCOMMENT, true);
                requestUrl.setParams(new String[] {
                        Const.COURSE_ID, mCourseId + "",
                        "rating", courseRatingBar.getRating() + "",
                        "content", courseInput.getText().toString()
                });
                mActivity.ajaxPost(requestUrl, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        mCommitBtn.setStatus(EdusohoButton.NORMAL);
                        Review review = mActivity.parseJsonValue(
                                object, new TypeToken<Review>(){});
                        if (review == null || review.id == 0) {
                            return;
                        }
                        mActivity.longToast("评论成功!");
                        mActivity.finish();
                        mActivity.app.sendMessage(Const.REFRESH_REVIEWS, null);
                    }
                });
            }
        });
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mCommitBtn = (EdusohoButton) view.findViewById(R.id.recommend_course_commitbtn);
        courseInput = (EditText) view.findViewById(R.id.recommend_course_input);
        courseRatingBar = (RatingBar) view.findViewById(R.id.recommend_course_rating);
        bindViewListener();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addCommentBtn()
    {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        TextView view  = null;

        View.OnClickListener quickClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseInput.setText(view.getTag(R.id.tag_first).toString());
            }
        };

        for (int i=0; i < Const.QUICK_COMMENTS.length; i++) {
            view = (TextView)inflater.inflate(R.layout.quick_comment_text, null);
            view.setText(Const.QUICK_COMMENTS[i].toString());
            view.setTag(R.id.tag_first, Const.QUICK_COMMENTS[i]);
            view.setTag(R.id.tag_second, Const.QUICK_COMMENTS[++i]);
            view.setOnClickListener(quickClickListener);
            //quickCommitBtn.addItem(view);
        }
    }
}
