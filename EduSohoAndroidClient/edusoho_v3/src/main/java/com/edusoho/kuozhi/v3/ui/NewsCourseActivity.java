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
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.edusoho.kuozhi.v3.view.EduSohoIconView;

import java.util.List;

/**
 * Created by JesseHuang on 15/9/16.
 */
public class NewsCourseActivity extends ActionBarBaseActivity {
    public static int CurrentCourseId = 0;
    public static final String COURSE_ID = "course_id";
    public static final String[] ACTIONS = {"查看详情", "进入学习"};
    private int mCourseId;

    private ListView lvCourseNews;
    private TextView tvStudyEntrance;
    private NewsCourseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_course);
        initViews();
        initDatas();
    }

    private void initViews() {
        lvCourseNews = (ListView) findViewById(R.id.lv_course_news);
        tvStudyEntrance = (TextView) findViewById(R.id.tv_study_entrance);
        tvStudyEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, Const.MY_LEARN);
                        startIntent.putExtra(WebViewActivity.URL, url);
                    }
                });
            }
        });
    }

    private void initDatas() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        setBackMode(BACK, intent.getStringExtra(Const.ACTIONBAR_TITLE));
        mCourseId = intent.getIntExtra(COURSE_ID, 0);
        NewsCourseDataSource newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<NewsCourseEntity> newsCourseEntityList = newsCourseDataSource.getNewsCourse(0, Const.LIMIT, mCourseId, app.loginUser.id);
        mAdapter = new NewsCourseAdapter(mContext, newsCourseEntityList);
        lvCourseNews.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.news_course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.news_course_profile) {
            //TODO 课时信息

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

            NewsCourseEntity newsCourseEntity = mList.get(position);
            viewHolder.tvTime.setText(AppUtil.convertMills2Date(((long) newsCourseEntity.getCreatedTime()) * 1000));
            viewHolder.tvLessonTitle.setText(newsCourseEntity.getContent());
            switch (newsCourseEntity.getLessonType()) {
                case PushUtil.LessonType.LIVE:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_live));
                    break;
                case PushUtil.LessonType.VIDEO:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_video));
                    break;
                case PushUtil.LessonType.AUDIO:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_audio));
                    break;
                case PushUtil.LessonType.TESTPAPER:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_testpaper));
                    break;
                case PushUtil.LessonType.PPT:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_ppt));
                    break;
                case PushUtil.LessonType.DOCUMENT:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_document));
                    break;
                case PushUtil.LessonType.FLASH:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_flash));
                    break;
                default:
                    //default is TEXT:
                    viewHolder.ivLessonType.setText(mContext.getString(R.string.font_lesson_type_text));
                    break;
            }

            viewHolder.tvLessonTitle.setText(newsCourseEntity.getContent());

            switch (newsCourseEntity.getBodyType()) {
                case PushUtil.CourseType.COURSE_ANNOUNCEMENT:
                    viewHolder.tvAction.setText(ACTIONS[0]);
                    viewHolder.tvLessonType.setText(PushUtil.CourseCode.COURSE_ANNOUNCEMENT);
                    break;
                case PushUtil.CourseType.TESTPAPER_REVIEWED:
                    viewHolder.tvAction.setText(ACTIONS[0]);
                    viewHolder.tvLessonType.setText(PushUtil.CourseCode.TESTPAPER_REVIEWED);
                    break;
                default:
                    //default is LESSON_PUBLISH:
                    viewHolder.tvAction.setText(ACTIONS[1]);
                    viewHolder.tvLessonType.setText(PushUtil.CourseCode.LESSON_PUBLISH);
                    break;
            }

            viewHolder.viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            return convertView;
        }
    }

    public static class ViewHolder {
        public TextView tvLessonType;
        public EduSohoIconView ivLessonType;
        public TextView tvLessonTitle;
        public TextView tvAction;
        private TextView tvTime;
        private View viewItem;

        public ViewHolder(View view) {
            tvLessonType = (TextView) view.findViewById(R.id.tv_lesson_type);
            tvLessonTitle = (TextView) view.findViewById(R.id.tv_lesson_title);
            ivLessonType = (EduSohoIconView) view.findViewById(R.id.iv_lesson_type);
            tvAction = (TextView) view.findViewById(R.id.tv_action);
            tvTime = (TextView) view.findViewById(R.id.tv_send_time);
            viewItem = view.findViewById(R.id.ll_news_course_item);
        }
    }
}
