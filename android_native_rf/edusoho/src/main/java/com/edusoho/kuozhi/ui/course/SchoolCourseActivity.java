package com.edusoho.kuozhi.ui.course;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.adapter.CourseMenuItemAdapter;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseMenu;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.common.CourseColumnActivity;
import com.edusoho.kuozhi.ui.common.SearchActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.kuozhi.view.OverScrollView;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.MoveListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.edusoho.kuozhi.adapter.CoursePagerAdapter;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.view.EdusohoListView;

import com.edusoho.kuozhi.R;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;


/**
 * 
 * @author howzhi
 * 
 */
public class SchoolCourseActivity extends BaseActivity {

	private static final int latest = 0;
	private static final int popular = 1;
	private static final int recommended = 2;

	private ViewPager content_pager;
	private RadioGroup head_radiogroup;
    public Activity mActivity;
    private LayoutInflater mInflater;
    private CourseMenu mCourseMenu;

    public static final String TAG = "SchoolCourseActivity";
    public static final String REFRESH_COURSE = "refresh";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        startSplash();
		setContentView(R.layout.sch_course);
        mActivity = this;
        mInflater = getLayoutInflater();
		initView();
		loadCoursePager();
		changeContentHead(popular);
        app.checkToken();
        app.addTask(TAG, this);
        addMessageListener();
        updateApp();
    }

    private void addMessageListener()
    {
        app.addMessageListener(REFRESH_COURSE, new CoreEngineMsgCallback() {
            @Override
            public void invoke(MessageModel messageModel) {
                refreshCourse((CourseMenu)messageModel.obj);
            }
        });
    }

    public void refreshCourse(CourseMenu courseMenu)
    {
        this.mCourseMenu = courseMenu;
        loadCoursePager();
        changeContentHead(popular);
    }

    public static void start(Activity context)
    {
        Activity schCourseActivity = EdusohoApp.runTask.get(TAG);
        if (schCourseActivity != null) {
            schCourseActivity.finish();
        }
        Intent intent = new Intent();
        intent.setClass(context, SchoolCourseActivity.class);
        context.startActivity(intent);
    }

    /**
	 *
	*/
	private void initView() {
		if (app.defaultSchool != null) {
            setBackMode(app.defaultSchool.name, false, null);
		}

        setActionBar();

		head_radiogroup = (RadioGroup) findViewById(R.id.head_radiogroup);
		content_pager = (ViewPager) findViewById(R.id.content_pager);

		head_radiogroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						int count = group.getChildCount();
						for (int i=0; i < count; i++) {
							RadioButton rb = (RadioButton) group.getChildAt(i);
							if (rb.getId() == checkedId) {
								changeContentHead(i);
								break;
							}
						}
			}
		});

        mCourseMenu = new CourseMenu("", "类别", "");
        app.addTask(TAG, this);
	}

    public void setActionBar()
    {
        setMenu(R.layout.schcourse_layout_menu, new MenuListener() {
            @Override
            public void bind(View menuView) {
                View sch_search_btn = menuView.findViewById(R.id.sch_search_btn);
                sch_search_btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SearchActivity.start(mActivity);
                    }
                });
            }
        });
    }

	/**
	 *
	*/
	private void loadCoursePager() {
		final ArrayList<View> mViewList = new ArrayList<View>();
		View pager = getLayoutInflater().inflate(R.layout.latest_course, null);
		mViewList.add(pager);

		pager = getLayoutInflater().inflate(R.layout.latest_course, null);
		mViewList.add(pager);

		pager = getLayoutInflater().inflate(R.layout.latest_course, null);
		mViewList.add(pager);

		CoursePagerAdapter adapter = new CoursePagerAdapter(mViewList) {
			@Override
			public void onPageSelected(int index) {
				changeContentHead(index);
				ViewGroup parent = (ViewGroup) mViewList.get(index);
                String tag = parent.getTag().toString();
                if ("false".equals(tag)) {
                    parent.setTag("true");
                    setPagerContent(parent);
                }
			}
		};
		content_pager.setAdapter(adapter);
		content_pager.setOnPageChangeListener(adapter);
	}

    private void setPagerContent(ViewGroup parent)
    {
        parent.removeAllViews();
        View course_content = mInflater.inflate(R.layout.course_content, null);
        parent.addView(course_content);
        loadCourse(parent, 0, false);
    }

	/**
	 * 
	 * @param index
	 */
	private void changeContentHead(int index) {
		if (index > head_radiogroup.getChildCount()) {
			return;
		}
		RadioButton rb = (RadioButton) head_radiogroup.getChildAt(index);
		rb.setChecked(true);
		content_pager.setCurrentItem(index);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
	 * 
	 * @param parent
	*/
	private void loadCourse(final ViewGroup parent, int page, final boolean isAppend)
	{
        final EdusohoListView listView = (EdusohoListView) parent.findViewById(R.id.course_liseview);

        StringBuffer param = new StringBuffer(Const.COURSE_LIST);
        param.append("?start=").append(page);
        param.append("&sort=").append(Const.SORT[content_pager.getCurrentItem()]);
        param.append("&channel=").append(mCourseMenu.type);
        String url = app.bindToken2Url(param.toString(), false);
        ajaxNormalGet(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                //hide loading layout
                parent.findViewById(R.id.load_layout).setVisibility(View.GONE);
                final CourseResult result = app.gson.fromJson(
                        object, new TypeToken<CourseResult>() {
                }.getType());

                if (result == null || result.data.length == 0) {
                    parent.findViewById(R.id.course_content_scrollview).setVisibility(View.GONE);
                    showEmptyLayout("暂无相应课程！");
                    return;
                }

                if (!isAppend) {
                    CourseListAdapter adapter = new CourseListAdapter(
                            mContext, result, R.layout.course_list_normal_item);
                    listView.setAdapter(adapter);
                    CourseListScrollListener listener = new CourseListScrollListener(mActivity, listView);
                    listView.setOnItemClickListener(listener);

                    OverScrollView scrollView = (OverScrollView) parent.findViewById(R.id.course_content_scrollview);
                    scrollView.setMoveListener(new MoveListener() {
                        @Override
                        public void moveToBottom() {
                            View course_more_btn = parent.findViewById(R.id.course_more_btn);
                            if (course_more_btn.getVisibility() == View.VISIBLE) {
                                course_more_btn.findViewById(R.id.more_btn_loadbar).setVisibility(View.VISIBLE);
                                Integer startPage = (Integer) parent.getTag();
                                loadCourse(parent, startPage, true);
                            }
                        }
                    });

                } else {
                    CourseListAdapter adapter = (CourseListAdapter) listView.getAdapter();
                    adapter.addItem(result);
                    listView.initListHeight();
                }

                View course_more_btn = parent.findViewById(R.id.course_more_btn);
                int start = result.start + Const.LIMIT;
                if (start < result.total) {
                    parent.setTag(start);
                    course_more_btn.setVisibility(View.VISIBLE);
                } else {
                    course_more_btn.setVisibility(View.GONE);
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                parent.findViewById(R.id.course_content_scrollview).setVisibility(View.GONE);
                showErrorLayout("网络数据加载错误！请重新尝试刷新。", new ListErrorListener() {
                    @Override
                    public void error(View errorBtn) {
                        setPagerContent(parent);
                    }
                });
                parent.findViewById(R.id.load_layout).setVisibility(View.GONE);
            }
        });
	}

}
