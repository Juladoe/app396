package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.StudyProcessRecyclerAdapter;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.PromiseCallback;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.courseDynamics.CourseDynamicsItem;
import com.edusoho.kuozhi.v3.model.bal.courseDynamics.DynamicsProvider;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.Promise;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JesseHuang on 15/12/14.
 */
public class CourseStudyFragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView studyProcessRecyclerView;
    private TextView mFloatButton;

    private StudyProcessRecyclerAdapter mAdapter;
    private RecyclerLinearLayoutManager mRecyclerLinearLayoutManager;

    private LinkedHashMap<String, List<NewsCourseEntity>> totalListMap;
    private List<NewsCourseEntity> dataList;
    private Bundle mBundle;
    private int mCourseId;
    private boolean isEndByLength = false;

    private NewsCourseDataSource newsCourseDataSource;
    private FrameLayout mLoading;

    private String[] types = {PushUtil.CourseType.TESTPAPER_REVIEWED,
            PushUtil.CourseType.QUESTION_ANSWERED,
            PushUtil.CourseType.HOMEWORK_REVIEWED,
            PushUtil.CourseType.LESSON_FINISH,
            PushUtil.CourseType.LESSON_START
    };

    List lessonIds = new ArrayList();
    List questionIds = new ArrayList();
    int lessonStartTime;

    private DynamicsProvider mDynamicsProvider;

    private View.OnClickListener summaryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.app.mEngine.runNormalPlugin("WebViewActivity", mContext, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    String url = String.format(Const.MOBILE_APP_URL, mActivity.app.schoolHost, String.format(Const.USER_LEARN_COURSE, mCourseId));
                    startIntent.putExtra(Const.WEB_URL, url);
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_course_study_process_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mRecyclerLinearLayoutManager = new RecyclerLinearLayoutManager(mContext);
        studyProcessRecyclerView = (RecyclerView) view.findViewById(R.id.study_process_list);
        studyProcessRecyclerView.setLayoutManager(mRecyclerLinearLayoutManager);
        studyProcessRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new StudyProcessRecyclerAdapter(mContext, new ArrayList(), app);
        mAdapter.setSummaryListene(summaryListener);
        studyProcessRecyclerView.setAdapter(mAdapter);

        mFloatButton = (TextView) view.findViewById(R.id.float_button);
        mFloatButton.setOnClickListener(this);

        mDynamicsProvider = new DynamicsProvider(mContext);

        mLoading = (FrameLayout) view.findViewById(R.id.study_dynamics_loading);

        initData();

    }

    public void initData() {
        mBundle = getArguments();
        mCourseId = mBundle.getInt("course_id");
        dataList = new ArrayList<NewsCourseEntity>();
        totalListMap = new LinkedHashMap<>();

        getDynamicsByNet().then(new PromiseCallback() {
            @Override
            public Promise invoke(Object obj) {
                filterData();

                studyProcessRecyclerView.scrollToPosition(findPosition());
                mLoading.setVisibility(View.GONE);
                return null;
            }
        });

    }

    public Promise getDynamicsByNet() {
        final Promise promise = new Promise();

        String subUrl = String.format(Const.COURSE_LEARNING_DYNAMICS, app.loginUser.id, mCourseId);
        RequestUrl requestUrl = app.bindNewApiUrl(subUrl, true);
        requestUrl.setGetParams(new String[]{"limit", "10000"});
        mDynamicsProvider.getDynamics(requestUrl).success(new NormalCallback<ArrayList<CourseDynamicsItem>>() {
            @Override
            public void success(ArrayList<CourseDynamicsItem> dynamicsItems) {
                promise.resolve(filterIntoEntity(dynamicsItems));
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                Toast.makeText(mContext, "网络问题或未知错误，请稍后再试", Toast.LENGTH_LONG).show();
            }
        });

        return promise;
    }

    public void filterData() {
        dataList = filterList(dataList);
        dataList = addLessonTitle(dataList);
        addCourseSummary();

        mAdapter.setmDataList(dataList);
    }

    public List<NewsCourseEntity> filterIntoEntity(ArrayList<CourseDynamicsItem> dynamicsItems) {
        Collections.reverse(dynamicsItems);
        for (CourseDynamicsItem dynamicsItem :
                dynamicsItems) {
            String type = dynamicsItem.getType();
            NewsCourseEntity entity = new NewsCourseEntity();
            if (dynamicsItem.getProperties().getLesson() != null) {
                entity.setContent(dynamicsItem.getProperties().getLesson().title);
                entity.setLessonId(dynamicsItem.getProperties().getLesson().id);
            }
            entity.setCourseId(mCourseId);
            entity.setCreatedTime(Integer.parseInt(dynamicsItem.getCreatedTime()));
            switch (type) {
                case "reviewed_homework":
                    entity.setBodyType("homework.reviewed");
                    dataList.add(entity);
                    break;

                case "reviewed_testpaper":
                    entity.setBodyType("testpaper.reviewed");
                    if (entity.getContent() == null){
                        break;
                    }
                    entity.setTitle(dynamicsItem.getProperties().getTestpaper().name);
                    entity.setObjectId(Integer.parseInt(dynamicsItem.getProperties().getResult().getId()));
                    dataList.add(entity);
                    break;

                case "start_learn_lesson":
                    entity.setBodyType("lesson.start");
                    dataList.add(entity);
                    break;

                case "learned_lesson":
                    entity.setBodyType("lesson.finish");
                    lessonStartTime = 0;
                    if (dynamicsItem.getProperties().getLessonLearnStartTime() != null) {
                        lessonStartTime = Integer.parseInt(dynamicsItem.getProperties().getLessonLearnStartTime());
                    }
                    entity.setLearnStartTime(lessonStartTime);
                    entity.setLearnFinishTime(Integer.parseInt(dynamicsItem.getCreatedTime()));
                    dataList.add(entity);
                    break;

                case "teacher_thread_post":
                    entity.setBodyType("question.answered");
                    entity.setContent(dynamicsItem.getProperties().getThread().getTitle());
                    entity.setLessonId(Integer.parseInt(dynamicsItem.getProperties().getThread().getLessonId()));
                    entity.setThreadId(Integer.parseInt(dynamicsItem.getProperties().getThread().getId()));
                    dataList.add(entity);
                    break;

                case "become_student":
                default:
                    break;

            }
        }
        return dataList;
    }

    private List<NewsCourseEntity> getNewsCourseList(int start) {
        List<NewsCourseEntity> entities = newsCourseDataSource.getNewsCourses(start, Const.STUDY_PROCESS_LIMIT, mCourseId, app.loginUser.id);
        Collections.reverse(entities);
        return entities;
    }

    public List filterList(List<NewsCourseEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            NewsCourseEntity entity = list.get(i);
            if (!Arrays.asList(types).contains(entity.getBodyType())) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    public List addLessonTitle(List<NewsCourseEntity> list) {
        lessonIds.clear();
        for (int i = 0; i < list.size(); i++) {
            NewsCourseEntity entity = list.get(i);
            String lessonId = entity.getLessonId() + "";
            String content = entity.getContent();
            if (lessonIds.contains(lessonId)) {
                totalListMap.get(lessonId).add(entity);
                while (++i < list.size()) {
                    if (list.get(i).getLessonId() == 0) {
                        totalListMap.get(lessonId).add(list.get(i));
                    } else {
                        break;
                    }
                }
                i--;
            } else {
                if (lessonId.equals("0")) {
                    int j = i;
                    do {
                        j++;
                        if (!(j < list.size())) {
                            isEndByLength = true;
                            break;
                        }
                        if (list.get(j).getLessonId() != 0) {
                            isEndByLength = false;
                            break;
                        }
                    } while (true);
                    List<NewsCourseEntity> subList = new ArrayList<>();
                    for (; i < j; i++) {
                        subList.add(list.get(i));
                    }
                    totalListMap.put("0", subList);
                    if (!isEndByLength) {
                        i--;
                    }
                } else {
                    NewsCourseEntity lessonTitleEntity = new NewsCourseEntity();
                    lessonTitleEntity.setContent(content);
                    lessonTitleEntity.setBodyType("course.lessonTitle");
                    lessonTitleEntity.setLessonId(Integer.parseInt(lessonId));
                    lessonTitleEntity.setCourseId(mCourseId);

                    NewsCourseEntity costTimeEntity = new NewsCourseEntity();
                    costTimeEntity.setContent("课时学习开始时间：" + AppUtil.timeStampToDate(entity.getCreatedTime() + "", null));
                    costTimeEntity.setBodyType("lesson.costTime");

                    List<NewsCourseEntity> subList = new ArrayList<>();
                    subList.add(lessonTitleEntity);
                    subList.add(costTimeEntity);
                    subList.add(entity);
                    while (++i < list.size()) {
                        if (list.get(i).getLessonId() == 0) {
                            subList.add(list.get(i));
                        } else {
                            break;
                        }
                    }
                    i--;
                    totalListMap.put(lessonId, subList);
                    lessonIds.add(lessonId);
                }
            }
        }

        list.clear();
        questionIds.clear();
        Iterator iterator = totalListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            List<NewsCourseEntity> subList = (List<NewsCourseEntity>) entry.getValue();
            filterUselessItem(subList);
            addFinishTime(subList);
            list.addAll(subList);
        }
        return list;
    }

    private void addFinishTime(List<NewsCourseEntity> list) {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            NewsCourseEntity entity = list.get(i);
            if (entity.getBodyType().equals("lesson.finish")) {
                if (list.get(list.size() - 1).getBodyType().equals("lesson.costTime")) {
                    continue;
                } else {
                    NewsCourseEntity finishTime = new NewsCourseEntity();
                    finishTime.setBodyType("lesson.costTime");
                    if (entity.getLearnStartTime() == 0) {
                        finishTime.setContent("");
                    } else {
                        finishTime.setContent("课时学习耗时：" + AppUtil.timeStampDiffToDay((entity.getLearnFinishTime() - entity.getLearnStartTime())));
                    }
                    list.add(finishTime);
                    for (int j = 0; j < i; j++) {
                        NewsCourseEntity tmpEntity = list.get(j);
                        if (tmpEntity.getBodyType().equals("course.lessonTitle")) {
                            tmpEntity.setIsLessonfinished(true);
                        }
                    }
                }
                for (int k = i + 1; k < list.size(); k++) {
                    NewsCourseEntity superFinishEntity = list.get(k);
                    if (superFinishEntity.getBodyType().equals("lesson.finish")) {
                        list.remove(k);
                        k--;
                    }
                }

            }
        }
    }

    private void filterUselessItem(List<NewsCourseEntity> list) {
        Collections.reverse(list);
        boolean hasHomework = false;
        for (int i = 0; i < list.size(); i++) {
            NewsCourseEntity entity = list.get(i);
            String type = entity.getBodyType();
            if (type.equals("question.answered")) {
                int questionId = entity.getThreadId();
                if (!questionIds.contains(questionId)) {
                    questionIds.add(questionId);
                } else {
                    list.remove(i);
                    i--;
                }
            } else if (type.equals("homework.reviewed")) {
                if (!hasHomework) {
                    hasHomework = true;
                } else {
                    list.remove(i);
                    i--;
                }
            }
        }
        Collections.reverse(list);
    }

    private void addCourseSummary() {
        RequestUrl requestUrl = app.bindUrl(Const.COURSE, false);
        HashMap<String, String> params = requestUrl.getParams();
        params.put("courseId", mCourseId + "");
        app.postUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CourseDetailsResult courseDetailsResult = mActivity.parseJsonValue(response, new TypeToken<CourseDetailsResult>() {
                });
                Course course = courseDetailsResult.course;
                if (dataList.size() != 0 && dataList.get(0).getBodyType().equals("course.summary")) {
                    return;
                } else {
                    NewsCourseEntity entity = new NewsCourseEntity();
                    entity.setBodyType("course.summary");
                    entity.setContent(course.about.equals("") ? "暂无课程简介" : course.about);
                    entity.setTeacher(course.teachers[0].nickname);
                    entity.setImage(course.smallPicture);
                    entity.setTitle(course.title);
                    mAdapter.notifyItemInserted(0);
                    dataList.add(0, entity);
                    mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private int findPosition() {
        if (dataList == null) {
            return 0;
        }
        Collections.reverse(dataList);
        int position = 0;
        for (int i = 0; i < dataList.size(); i++) {
            NewsCourseEntity entity = dataList.get(i);
            if (entity.getBodyType().equals("course.lessonTitle")) {
                position = dataList.size() - i;
                break;
            }
        }
        Collections.reverse(dataList);
        return position - 1;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.float_button) {
            app.mEngine.runNormalPlugin("ThreadDiscussActivity", mActivity, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(ThreadDiscussActivity.COURSE_ID, mCourseId);
                    startIntent.putExtra(ThreadDiscussActivity.ACTIVITY_TYPE, PushUtil.ThreadMsgType.THREAD);
                }
            });
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
            dataList.add(entity);
            filterData();
            mAdapter.notifyItemRangeChanged(0, mAdapter.getItemCount());
        }
    }

    private static class RecyclerLinearLayoutManager extends LinearLayoutManager {

        public RecyclerLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}
