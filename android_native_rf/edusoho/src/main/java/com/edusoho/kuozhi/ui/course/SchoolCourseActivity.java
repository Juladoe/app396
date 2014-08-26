package com.edusoho.kuozhi.ui.course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
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
import com.edusoho.kuozhi.model.School;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
    private HashMap<Integer, CourseListAdapter> adapterHashMap;

    public static final String TAG = "SchoolCourseActivity";
    public static final String REFRESH_COURSE = "refresh";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sch_course);
        mActivity = this;
        mInflater = getLayoutInflater();
		initView();
		loadCoursePager();
		changeContentHead(popular);
        app.checkToken();
        addMessageListener();
        updateApp();
        logSchoolInfoToServer();
    }

    private void logSchoolInfoToServer()
    {
        Map<String, String> params = app.getPlatformInfo();
        School school = app.defaultSchool;
        params.put("name", school.name);
        params.put("host", school.host);
        app.logToServer("http://open.edusoho.com/mobile/mobile_login_stat.php", params, null);
        app.logToServer(app.schoolHost + Const.MOBILE_SCHOOL_LOGIN, params, null);
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
        String name = "".equals(courseMenu.name) ? "" : "-" + courseMenu.name;
        changeTitle("好知网" + name);
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

        adapterHashMap = new HashMap<Integer, CourseListAdapter>();
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
                        app.mEngine.runNormalPlugin("SearchActivity", mActivity, null);
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
		ViewGroup pager = (ViewGroup) getLayoutInflater().inflate(R.layout.latest_course, null);
		mViewList.add(pager);
        View course_content = mInflater.inflate(R.layout.course_content, null);
        pager.addView(course_content);

		pager = (ViewGroup) getLayoutInflater().inflate(R.layout.latest_course, null);
		mViewList.add(pager);
        course_content = mInflater.inflate(R.layout.course_content, null);
        pager.addView(course_content);

		pager = (ViewGroup) getLayoutInflater().inflate(R.layout.latest_course, null);
		mViewList.add(pager);
        course_content = mInflater.inflate(R.layout.course_content, null);
        pager.addView(course_content);

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

    /**
	 * 
	 * @param parent
	*/
	private void loadCourse(final ViewGroup parent, int page, final boolean isAppend)
	{
        final PullToRefreshListView pullToRefreshListView = (PullToRefreshListView) parent.findViewById(R.id.course_liseview);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        final ListView listView = pullToRefreshListView.getRefreshableView();

        StringBuffer param = new StringBuffer(Const.COURSES);
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
                    showEmptyLayout("暂无相应课程！");
                    return;
                }

                if (!isAppend) {
                    CourseListAdapter adapter = new CourseListAdapter(
                            mContext, result, R.layout.course_list_normal_item);
                    adapterHashMap.put(content_pager.getCurrentItem(), adapter);
                    listView.setAdapter(adapter);
                    CourseListScrollListener listener = new CourseListScrollListener(mActivity);
                    listView.setOnItemClickListener(listener);
                    pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            loadCourse(parent, 0, false);
                        }

                        @Override
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            Integer startPage = (Integer) parent.getTag();
                            loadCourse(parent, startPage, true);
                        }
                    });
                } else {
                    CourseListAdapter adapter = (CourseListAdapter) adapterHashMap.get(content_pager.getCurrentItem());
                    adapter.addItem(result);
                }

                pullToRefreshListView.onRefreshComplete();
                int start = result.start + Const.LIMIT;
                if (start < result.total) {
                    parent.setTag(start);
                } else {
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                int currentIndex = content_pager.getCurrentItem();
                CourseListAdapter adapter = adapterHashMap.get(currentIndex);
                if (adapter != null && adapter.getCount() > 0) {
                    longToast("网络数据加载错误！请重新尝试刷新。");
                    return;
                }
                showErrorLayout("网络数据加载错误！请重新尝试刷新。", new ListErrorListener() {
                    @Override
                    public void error(View errorBtn) {
                        parent.removeAllViews();
                        View course_content = mInflater.inflate(R.layout.course_content, null);
                        parent.addView(course_content);
                        setPagerContent(parent);
                    }
                });
                parent.findViewById(R.id.load_layout).setVisibility(View.GONE);
            }
        });
	}

}
