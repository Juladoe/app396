package com.edusoho.kuozhi.v3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.DiscussFragment;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.google.gson.reflect.TypeToken;

/**
 * Created by DF on 2017/1/5.
 */

public class RefreshListView extends ListView{

    public LinearLayout mFooterView;
    public int start = 20;
    public boolean isRequest = true;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initWithContext(final BaseActivity baseActivity, final DiscussFragment discussFragment, final String mCouseId) {
        mFooterView = new RefreshFooter(baseActivity);
        addFooterView(mFooterView);
        mFooterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequest) {
                    discussFragment.mLoadView.setVisibility(VISIBLE);
                    RequestUrl requestUrl = baseActivity.app.bindNewUrl(String.format(Const.LESSON_DISCUSS, mCouseId, mCouseId, start + ""), true);
                    baseActivity.app.getUrl(requestUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            start += 20;
                            hideLoadView(discussFragment);
                            DiscussDetail moreDiscuss = baseActivity.parseJsonValue(response, new TypeToken<DiscussDetail>() {});
                            if (moreDiscuss.getResources() != null) {
                                discussFragment.discussDetail.getResources().addAll(moreDiscuss.getResources());
                                discussFragment.catalogueAdapter.notifyDataSetChanged();
                            }
                            if (moreDiscuss.getResources().size() < 20) {
                                isRequest = false;
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            discussFragment.mLoadView.setVisibility(View.GONE);
                            discussFragment.setLessonEmptyViewVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    CommonUtil.shortCenterToast(getContext(), "已加载完");
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void hideLoadView(final DiscussFragment discussFragment){
        postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonUtil.shortCenterToast(getContext(), "加载成功");
                discussFragment.mLoadView.setVisibility(GONE);
            }
        }, 2000);
    }

}
