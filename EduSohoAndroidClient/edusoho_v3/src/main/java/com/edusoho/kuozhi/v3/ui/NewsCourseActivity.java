package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.ui.fragment.NewsFragment;
import com.edusoho.kuozhi.v3.ui.fragment.test.TestpaperResultFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.NotificationUtil;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;

import java.util.Collections;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseActivity extends ActionBarBaseActivity {
    public static int CurrentCourseId = 0;
    public static final String COURSE_ID = "course_id";
    public static final String[] ACTIONS = {"查看详情", "进入学习", "进入直播"};
    private int mCourseId;
    private int mStart = 0;

    private PtrClassicFrameLayout mPtrFrame;
    private ListView lvCourseNews;
    private TextView tvStudyEntrance;
    private NewsCourseAdapter mAdapter;
    private NewsCourseDataSource newsCourseDataSource;
    private android.os.Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_course);
        initViews();
        initDatas();
    }

    private void initViews() {
        mHandler = new android.os.Handler();
        lvCourseNews = (ListView) findViewById(R.id.lv_course_news);
        tvStudyEntrance = (TextView) findViewById(R.id.tv_study_entrance);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);
        tvStudyEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.USER_LEARN_COURSE, mCourseId));
                        startIntent.putExtra(WebViewActivity.URL, url);
                    }
                });
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mStart = 0;
        List<NewsCourseEntity> newsCourseEntityList = getNewsCourseList(mStart);
        mStart = newsCourseEntityList.size();
        mAdapter = new NewsCourseAdapter(mContext, newsCourseEntityList);
        lvCourseNews.setAdapter(mAdapter);
        lvCourseNews.postDelayed(mListViewSelectRunnable, 100);
    }

    private void initDatas() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));
        mCourseId = intent.getIntExtra(COURSE_ID, 0);
        CurrentCourseId = mCourseId;
        if (mCourseId == 0) {
            CommonUtil.longToast(getApplicationContext(), getString(R.string.course_params_error));
            return;
        }
        newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<NewsCourseEntity> newsCourseEntityList = getNewsCourseList(mStart);
        mAdapter = new NewsCourseAdapter(mContext, newsCourseEntityList);
        lvCourseNews.setAdapter(mAdapter);
        mStart = mAdapter.getCount();
        lvCourseNews.postDelayed(mListViewSelectRunnable, 100);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mAdapter.addItems(getNewsCourseList(mStart));
                mStart = mAdapter.getCount();
                mPtrFrame.refreshComplete();
                lvCourseNews.postDelayed(mListViewSelectRunnable, 100);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean canDoRefresh = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                int count = 0;
                if (canDoRefresh) {
                    count = getNewsCourseList(mStart).size();
                }
                return count > 0 && canDoRefresh;
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNewFragment2UpdateItemBadge();
            }
        }, 500);
        NotificationUtil.cancelById(mCourseId);
    }

    private void sendNewFragment2UpdateItemBadge() {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.FROM_ID, mCourseId);
        app.sendMsgToTarget(NewsFragment.UPDATE_UNREAD_NEWS_COURSE, bundle, NewsFragment.class);
    }

    private List<NewsCourseEntity> getNewsCourseList(int start) {
        List<NewsCourseEntity> entities = newsCourseDataSource.getNewsCourses(start, Const.NEWS_LIMIT, mCourseId, app.loginUser.id);
        Collections.reverse(entities);
        return entities;
    }

    private Runnable mListViewSelectRunnable = new Runnable() {
        @Override
        public void run() {
            lvCourseNews.setSelection(mStart);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news_course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_course_profile) {
            app.mEngine.runNormalPlugin("NewsCourseProfileActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(Const.COURSE_ID, mCourseId);
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    public class NewsCourseAdapter extends BaseAdapter {
        private Context mContext;
        private List<NewsCourseEntity> mList;

        public NewsCourseAdapter(Context context, List<NewsCourseEntity> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }


        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            if (mList != null) {
                mList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void addItem(NewsCourseEntity entity) {
            mList.add(entity);
            notifyDataSetChanged();
        }

        public void addItems(List<NewsCourseEntity> entities) {
            mList.addAll(0, entities);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_course, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final NewsCourseEntity newsCourseEntity = mList.get(position);
            viewHolder.tvTime.setText(AppUtil.convertMills2Date(((long) newsCourseEntity.getCreatedTime()) * 1000));
            View.OnClickListener itemClickListener = null;
            switch (newsCourseEntity.getBodyType()) {
                case PushUtil.CourseType.COURSE_ANNOUNCEMENT:
                    viewHolder.tvAction.setText(ACTIONS[0]);
                    viewHolder.tvContent.setText(newsCourseEntity.getContent());
                    viewHolder.tvLessonType.setText(PushUtil.CourseCode.COURSE_ANNOUNCEMENT);
                    viewHolder.ivLessonType.setText(getString(R.string.font_announcement));
                    viewHolder.ivLessonType.setBackgroundColor(getResources().getColor(R.color.orange_alpha));
                    itemClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.ANNOUNCEMENT, newsCourseEntity.getCourseId()));
                                    startIntent.putExtra(WebViewActivity.URL, url);
                                }
                            });
                        }
                    };
                    break;
                case PushUtil.CourseType.TESTPAPER_REVIEWED:
                    viewHolder.tvAction.setText(ACTIONS[0]);
                    viewHolder.tvContent.setText(newsCourseEntity.getContent());
                    viewHolder.tvLessonType.setText(PushUtil.CourseCode.TESTPAPER_REVIEWED);
                    viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_testpaper));
                    viewHolder.ivLessonType.setBackgroundColor(getResources().getColor(R.color.green_alpha));
                    itemClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            app.mEngine.runNormalPlugin("FragmentPageActivity", mActivity, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
                                    startIntent.putExtra(Const.ACTIONBAR_TITLE, newsCourseEntity.getTitle() + "考试结果");
                                    startIntent.putExtra(TestpaperResultFragment.RESULT_ID, newsCourseEntity.getObjectId());
                                    startIntent.putExtra(Const.STATUS, "finished");
                                }
                            });
                        }
                    };
                    break;
                case PushUtil.CourseType.LESSON_PUBLISH:
                    viewHolder.tvAction.setText(ACTIONS[1]);
                    viewHolder.tvContent.setText(newsCourseEntity.getContent());
                    viewHolder.tvLessonType.setText(PushUtil.CourseCode.LESSON_PUBLISH);
                    viewHolder.ivLessonType.setBackgroundColor(getResources().getColor(R.color.blue_alpha));
                    switch (newsCourseEntity.getLessonType()) {
                        case PushUtil.LessonType.LIVE:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_live));
                            break;
                        case PushUtil.LessonType.VIDEO:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_video));
                            break;
                        case PushUtil.LessonType.AUDIO:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_audio));
                            break;
                        case PushUtil.LessonType.TESTPAPER:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_testpaper));
                            break;
                        case PushUtil.LessonType.PPT:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_ppt));
                            break;
                        case PushUtil.LessonType.DOCUMENT:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_document));
                            break;
                        case PushUtil.LessonType.FLASH:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_flash));
                            break;
                        case PushUtil.LessonType.TEXT:
                            viewHolder.ivLessonType.setText(getString(R.string.font_lesson_type_text));
                            break;
                        default:
                            break;
                    }

                    itemClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            app.mEngine.runNormalPlugin(
                                    LessonActivity.TAG, mActivity, new PluginRunCallback() {
                                        @Override
                                        public void setIntentDate(Intent startIntent) {
                                            startIntent.putExtra(Const.COURSE_ID, newsCourseEntity.getCourseId());
                                            startIntent.putExtra(Const.LESSON_ID, newsCourseEntity.getObjectId());
                                        }
                                    }
                            );
                        }
                    };
                    break;
                case PushUtil.CourseType.LIVE_NOTIFY:
                    viewHolder.tvAction.setText(ACTIONS[2]);
                    viewHolder.tvContent.setText(newsCourseEntity.getContent());
                    viewHolder.tvLessonType.setText(PushUtil.CourseCode.Lesson_LIVE_NOTIFY);
                    viewHolder.ivLessonType.setText(getString(R.string.font_lesson_live_start_notify));
                    viewHolder.ivLessonType.setBackgroundColor(getResources().getColor(R.color.red));
                    itemClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            app.mEngine.runNormalPlugin(
                                    LessonActivity.TAG, mActivity, new PluginRunCallback() {
                                        @Override
                                        public void setIntentDate(Intent startIntent) {
                                            startIntent.putExtra(Const.COURSE_ID, newsCourseEntity.getCourseId());
                                            startIntent.putExtra(Const.LESSON_ID, newsCourseEntity.getObjectId());
                                        }
                                    }
                            );
                        }
                    };
                    break;
                default:
                    break;
            }

            viewHolder.viewItem.setOnClickListener(itemClickListener);

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView tvLessonType;
        public EduSohoIconView ivLessonType;
        public TextView tvContent;
        public TextView tvAction;
        private TextView tvTime;
        private View viewItem;

        public ViewHolder(View view) {
            tvLessonType = (TextView) view.findViewById(R.id.tv_lesson_type);
            tvContent = (TextView) view.findViewById(R.id.tv_lesson_content);
            ivLessonType = (EduSohoIconView) view.findViewById(R.id.iv_lesson_type);
            tvAction = (TextView) view.findViewById(R.id.tv_action);
            tvTime = (TextView) view.findViewById(R.id.tv_send_time);
            viewItem = view.findViewById(R.id.ll_news_course_item);
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        return new MessageType[]{new MessageType(Const.ADD_COURSE_MSG, source)};
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.ADD_COURSE_MSG == messageType.code) {
            WrapperXGPushTextMessage wrapperMessage = (WrapperXGPushTextMessage) message.data.get(Const.GET_PUSH_DATA);
            NewsCourseEntity entity = new NewsCourseEntity(wrapperMessage);
            mAdapter.addItem(entity);
        }
    }
}
