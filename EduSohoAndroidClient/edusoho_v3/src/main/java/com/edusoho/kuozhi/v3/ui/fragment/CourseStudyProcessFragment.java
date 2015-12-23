package com.edusoho.kuozhi.v3.ui.fragment;

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
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailsResult;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.PushUtil;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by melomelon on 15/12/9.
 */
public class CourseStudyProcessFragment extends BaseFragment {

    private RecyclerView studyProcessRecyclerView;
    private ImageButton mFloatButton;

    private StudyProcessRecyclerAdapter mAdapter;

    private LinkedHashMap<String, List<NewsCourseEntity>> totalListMap;
    private int listCount = 0;
    private List<NewsCourseEntity> dataList;
    private Bundle mBundle;
    private int mCourseId;

    private NewsCourseDataSource newsCourseDataSource;

    private String[] types = {PushUtil.CourseType.TESTPAPER_REVIEWED,
            PushUtil.CourseType.QUESTION_ANSWERED,
            PushUtil.CourseType.HOMEWORK_REVIEWED,
            PushUtil.CourseType.LESSON_FINISH
    };

    List lessonIds = new ArrayList();
    List<String> lessonTitles = new ArrayList<String>();

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
        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 15/12/17 跳转到提问界面 
            }
        });

        initData();
//        studyProcessRecyclerView.scrollToPosition(dataList.size());

    }

    public void initData() {
        mBundle = getArguments();
        mCourseId = mBundle.getInt("course_id");
        newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        totalListMap = new LinkedHashMap<>();
        dataList = getNewsCourseList(0);
        dataList = filterList(dataList);
        dataList = addLessonTitle(dataList);
        addCourseSummary(dataList);
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
        for (int i = 0; i < list.size(); i++) {
            NewsCourseEntity entity = list.get(i);
            String lessonId = entity.getLessonId() + "";
            String content = entity.getContent();
            if (entity.getBodyType().equals(PushUtil.CourseType.TESTPAPER_REVIEWED)) {
                if (lessonTitles.contains(content)) {
                    totalListMap.get(content).add(entity);
                    continue;
                }else {
                    NewsCourseEntity newsCourseEntity = new NewsCourseEntity();
                    newsCourseEntity.setContent(content);
                    newsCourseEntity.setBodyType("course.lessonTitle");
                    newsCourseEntity.setObjectId(Integer.parseInt(lessonId));
                    newsCourseEntity.setCourseId(mCourseId);

                    List<NewsCourseEntity> subList = new ArrayList<>();
                    subList.add(newsCourseEntity);
                    subList.add(entity);

                    totalListMap.put(content, subList);
                    lessonTitles.add(content);
                }

            } else if (lessonIds.contains(lessonId)) {
                totalListMap.get(lessonId).add(entity);
            } else {
                NewsCourseEntity newsCourseEntity = new NewsCourseEntity();
                newsCourseEntity.setContent(content);
                newsCourseEntity.setBodyType("course.lessonTitle");
                newsCourseEntity.setObjectId(Integer.parseInt(lessonId));
                newsCourseEntity.setCourseId(mCourseId);

                List<NewsCourseEntity> subList = new ArrayList<>();
                subList.add(newsCourseEntity);
                subList.add(entity);

                totalListMap.put(lessonId, subList);
                lessonIds.add(lessonId);
            }
        }
        list.clear();
        Iterator iterator = totalListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            list.addAll((Collection<? extends NewsCourseEntity>) entry.getValue());
        }
        return list;
    }

    private void addCourseSummary(final List<NewsCourseEntity> entities) {
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
