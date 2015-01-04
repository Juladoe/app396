package com.edusoho.kuozhi.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Course.FoundCourseListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Category;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.course.CourseListActivity;
import com.edusoho.kuozhi.ui.widget.CategoryListView;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.HashMap;

import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-8-14.
 */

public class FoundFragment extends BaseFragment {

    private CategoryListView mCategoryListView;
    private int mCategoryHeight;
    private int mCurrentCategoryId;
    private ImageView mSelectIconView;
    private RefreshListWidget mCourseListView;

    public static final int HIDE_ACTION_BAR_CODE = 0001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSelectIconView = mActivity.addTitleViewIcon(R.drawable.found_select_icon);
        setContainerView(R.layout.found_layout);
        Log.d(null, "onCreate");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.found_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.found_menu_search) {
            SearchDialogFragment searchDialogFragment = new SearchDialogFragment();
            searchDialogFragment.show(getChildFragmentManager(), "dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        int type = message.type.code;
        switch (type) {
            case HIDE_ACTION_BAR_CODE:
                //showSearchLayout();
                break;
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(HIDE_ACTION_BAR_CODE, source)
        };
        return messageTypes;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(null, "onResume");
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    protected void initView(View view) {
        mCategoryListView = (CategoryListView) view.findViewById(R.id.found_category_list);
        mCourseListView = (RefreshListWidget) view.findViewById(R.id.found_category_course_list);
        mCourseListView.setEmptyText(new String[] { "没有搜到相关课程" }, R.drawable.icon_course_empty);
        mCourseListView.setAdapter(new FoundCourseListAdapter(mContext, R.layout.found_course_list_item));
        mCourseListView.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadCourseList(mCurrentCategoryId, mCourseListView.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadCourseList(mCurrentCategoryId, 0);
            }
        });

        mActivity.setTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCategoryListView.getHeight() > 0) {
                    hideCategoryList();
                } else {
                    showCategoryList();
                }
            }
        });

        mCourseListView.setLoadAdapter();
        changeTitle("全部");
        loadCourseList(0, 0);
        mCourseListView.setOnItemClickListener(new CourseListScrollListener(mActivity));

        RequestUrl url = app.bindUrl(Const.CATEGORYS, false);
        mCategoryListView.initialise(mActivity, url);

        mCategoryListView.setItemClick(new CategoryListView.ItemClickListener() {
            @Override
            public void click(final Category category) {
                changeTitle(category.id == 0 ? "全部" : category.name);
                mCourseListView.setLoadAdapter();
                loadCourseList(category.id, 0);
                hideCategoryList();
            }
        });
    }

    private void rotation(View view, float start, float end)
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", start, end);
        objectAnimator.setDuration(180);
        objectAnimator.start();
    }

    private void hideCategoryList()
    {
        mCategoryHeight = mCourseListView.getHeight();
        AppUtil.animForHeight(new EdusohoAnimWrap(mCategoryListView), mCategoryHeight, 0, 240);
        rotation(mSelectIconView, -180, 0);
    }

    private void showCategoryList()
    {
        mCategoryHeight = mCourseListView.getHeight();
        AppUtil.animForHeight(new EdusohoAnimWrap(mCategoryListView), 0, mCategoryHeight, 180);
        mCategoryListView.scrollToTop();
        rotation(mSelectIconView, 0, -180);
    }

    private void loadCourseList(int categoryId, int start)
    {
        Log.d(null, "categoryId->" + categoryId);
        mCurrentCategoryId = categoryId;
        RequestUrl url = app.bindUrl(Const.COURSES, true);
        HashMap<String, String> params = url.getParams();
        params.put(CourseListActivity.CATEGORY_ID, String.valueOf(categoryId));
        params.put("start", String.valueOf(start));
        params.put("limit", String.valueOf(Const.LIMIT));

        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mCourseListView.onRefreshComplete();
                CourseResult courseResult = mActivity.gson.fromJson(
                        object, new TypeToken<CourseResult>() {
                        }.getType());

                if (courseResult == null) {
                    return;
                }

                mCourseListView.pushData(courseResult.data);
                mCourseListView.setStart(courseResult.start, courseResult.total);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(null, "onDestroyView");
    }
}
