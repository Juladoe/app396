package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MyAskAdapter;
import com.edusoho.kuozhi.v3.adapter.MyCacheAdapter;
import com.edusoho.kuozhi.v3.adapter.MyCollectAdapter;
import com.edusoho.kuozhi.v3.adapter.MyStudyAdapter;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by remilia on 2017/1/5.
 */
public class MyTabFragment extends BaseFragment {

    private View mLayoutFilter;
    private TextView mTvFilterName;
    private View mLayoutFilterName;
    private TextView mTvFilterArrow;
    private ListView mLvContent;
    private LinearLayout mLayoutDesc;

    private MyAskAdapter mMyAskAdapter;
    private MyCacheAdapter mMyCacheAdapter;
    private MyCollectAdapter mMyCollectAdapter;
    private MyStudyAdapter mMyStudyAdapter;

    public static final String TYPE = "type";
    public static final int TYPE_STUDY = 0;
    public static final int TYPE_CACHE = 1;
    public static final int TYPE_COLLECT = 2;
    public static final int TYPE_ASK = 3;

    private int mType = TYPE_STUDY;

    public static final String[] TYPE_STUDY_DESC = {"最近", "课程", "直播", "班级"};
    public static final String[] TYPE_ASK_DESC = {"发起", "回复"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_my_tab);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getInt(TYPE, TYPE_STUDY);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mLayoutFilter = view.findViewById(R.id.layout_filter);
        mTvFilterName = (TextView) view.findViewById(R.id.tv_filter_name);
        mLayoutFilterName = view.findViewById(R.id.layout_filter_name);
        mTvFilterArrow = (TextView) view.findViewById(R.id.tv_filter_arrow);
        mLvContent = (ListView) view.findViewById(R.id.lv_content);
        mLayoutDesc = (LinearLayout) view.findViewById(R.id.layout_filter_desc);
        switch (mType) {
            case TYPE_STUDY:
                mLayoutFilter.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params1 = mLayoutDesc.getLayoutParams();
                if (params1 != null) {
                    params1.width = -1;
                    mLayoutDesc.setLayoutParams(params1);
                }
                for (int i = 0; i < 4; i++) {
                    TextView child = new TextView(getActivity());
                    child.setGravity(Gravity.CENTER);
                    child.setText(TYPE_STUDY_DESC[i]);
                    child.setTextSize(15);
                    if (i == 0) {
                        child.setTextColor(getResources().getColor(R.color.primary_color));
                    } else {
                        child.setTextColor(getResources().getColor(R.color.primary_font_color));
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1);
                    params.weight = 1;
                    child.setLayoutParams(params);
                    mLayoutDesc.addView(child);
                }
                mMyStudyAdapter = new MyStudyAdapter(getActivity(), 0);
                mLvContent.setAdapter(mMyStudyAdapter);
                mMyStudyAdapter.setType(0);
                mTvFilterName.setText(TYPE_STUDY_DESC[0]);
                break;
            case TYPE_CACHE:
                mLayoutFilter.setVisibility(View.GONE);
                mMyCacheAdapter = new MyCacheAdapter();
                mLvContent.setAdapter(mMyCacheAdapter);
                break;
            case TYPE_COLLECT:
                mLayoutFilter.setVisibility(View.GONE);
                mMyCollectAdapter = new MyCollectAdapter(getActivity());
                mLvContent.setAdapter(mMyCollectAdapter);
                break;
            case TYPE_ASK:
                mLayoutFilter.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params2 = mLayoutDesc.getLayoutParams();
                if (params2 != null) {
                    params2.width = AppUtil.getWidthPx(getActivity()) / 2;
                    mLayoutDesc.setLayoutParams(params2);
                }
                for (int i = 0; i < 2; i++) {
                    TextView child = new TextView(getActivity());
                    child.setGravity(Gravity.CENTER);
                    child.setText(TYPE_ASK_DESC[i]);
                    child.setTextSize(15);
                    if (i == 0) {
                        child.setTextColor(getResources().getColor(R.color.primary_color));
                    } else {
                        child.setTextColor(getResources().getColor(R.color.primary_font_color));
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1);
                    params.weight = 1;
                    child.setLayoutParams(params);
                    mLayoutDesc.addView(child);
                }
                mMyAskAdapter = new MyAskAdapter(getActivity(), 0);
                mLvContent.setAdapter(mMyAskAdapter);
                mMyAskAdapter.setType(0);
                break;
        }
        initEvent();
    }

    private void initEvent() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                int length = mLayoutDesc.getChildCount();
                for (int i = 0; i < length; i++) {
                    View view = mLayoutDesc.getChildAt(i);
                    if (view != null && view instanceof TextView) {
                        if (position == i) {
                            ((TextView) view).setTextColor(
                                    getResources().getColor(R.color.primary_color));
                        } else {
                            ((TextView) view).setTextColor(
                                    getResources().getColor(R.color.primary_font_color));
                        }
                    }
                }
                switch (mType) {
                    case TYPE_STUDY:
                        mMyStudyAdapter.setType(position);
                        mTvFilterName.setText(TYPE_STUDY_DESC[position]);
                        break;
                    case TYPE_ASK:
                        mMyAskAdapter.setType(position);
                        mTvFilterName.setText(TYPE_ASK_DESC[position]);
                        break;
                }
                mTvFilterArrow.setText(R.string.new_font_unfold);
                mLayoutDesc.setVisibility(View.GONE);
            }
        };
        int length = mLayoutDesc.getChildCount();
        for (int i = 0; i < length; i++) {
            View view = mLayoutDesc.getChildAt(i);
            if (view != null) {
                view.setTag(i);
                view.setOnClickListener(onClickListener);
            }
        }
        mLayoutFilterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getString(R.string.new_font_unfold)
                        .equals(mTvFilterArrow.getText())) {
                    mTvFilterArrow.setText(R.string.new_font_fold);
                    mLayoutDesc.setVisibility(View.VISIBLE);
                } else {
                    mTvFilterArrow.setText(R.string.new_font_unfold);
                    mLayoutDesc.setVisibility(View.GONE);
                }
            }
        });
    }

}
