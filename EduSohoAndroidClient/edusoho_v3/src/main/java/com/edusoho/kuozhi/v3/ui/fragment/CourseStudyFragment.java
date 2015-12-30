package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.StudyProcessRecyclerAdapter;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.ThreadDiscussActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
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
    private ImageButton mFloatButton;

    private StudyProcessRecyclerAdapter mAdapter;

    private LinkedHashMap<String, List<NewsCourseEntity>> totalListMap;
    private List<NewsCourseEntity> dataList;
    private Bundle mBundle;
    private int mCourseId;
    private boolean isEndByLength = false;

    private NewsCourseDataSource newsCourseDataSource;

    private String[] types = {PushUtil.CourseType.TESTPAPER_REVIEWED,
            PushUtil.CourseType.QUESTION_ANSWERED,
            PushUtil.CourseType.HOMEWORK_REVIEWED,
            PushUtil.CourseType.LESSON_FINISH,
            PushUtil.CourseType.LESSON_START
    };

    List lessonIds = new ArrayList();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_course_study_process_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        studyProcessRecyclerView = (RecyclerView) view.findViewById(R.id.study_process_list);
        studyProcessRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        studyProcessRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFloatButton = (ImageButton) view.findViewById(R.id.float_button);
        mFloatButton.setOnClickListener(this);

        initData();
        filterData();
    }

    public void initData() {
        mBundle = getArguments();
        mCourseId = mBundle.getInt("course_id");
        newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        totalListMap = new LinkedHashMap<>();
        dataList = getNewsCourseList(0);
    }

    public void filterData() {
        dataList = filterList(dataList);
        dataList = addLessonTitle(dataList);
        addCourseSummary();
        mAdapter = new StudyProcessRecyclerAdapter(mContext, dataList, app);
        studyProcessRecyclerView.setAdapter(mAdapter);
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
        Iterator iterator = totalListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            List<NewsCourseEntity> subList = (List<NewsCourseEntity>) entry.getValue();
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
                    finishTime.setContent("课时学习耗时：" + AppUtil.timeStampDiffToDay((entity.getLearnFinishTime() - entity.getLearnStartTime())));
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
                NewsCourseEntity entity = new NewsCourseEntity();
                entity.setBodyType("course.summary");
                entity.setContent(course.about.equals("") ? "暂无课程简介" : course.about);
                entity.setTeacher(course.teachers[0].nickname);
                entity.setImage(course.smallPicture);
                entity.setTitle(course.title);
                dataList.add(0, entity);
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.float_button) {
            app.mEngine.runNormalPlugin("ThreadDiscussActivity", mActivity, new PluginRunCallback() {
                @Override
                public void setIntentDate(Intent startIntent) {
                    startIntent.putExtra(ThreadDiscussActivity.COURSE_ID, mCourseId);
                    startIntent.putExtra(ThreadDiscussActivity.LESSON_ID, 0);
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
            initData();
            dataList.add(entity);
            filterData();
            mAdapter.notifyDataSetChanged();
        }
    }
}
