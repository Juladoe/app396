package com.edusoho.kuozhi.v3.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.StudyProcessRecyclerAdapter;
import com.edusoho.kuozhi.v3.model.bal.push.NewsCourseEntity;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.sql.NewsCourseDataSource;
import com.edusoho.kuozhi.v3.util.sql.SqliteChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by melomelon on 15/12/9.
 */
public class CourseStudyProcessFragment extends BaseFragment{

    private RecyclerView studyProcessRecyclerView;

    private StudyProcessRecyclerAdapter mAdapter;

    private List<NewsCourseEntity> dataList;
    private Bundle mBundle;
    private int mCourseId;

    private NewsCourseDataSource newsCourseDataSource;

    private String[] types = {"testpaper.reviewed","announcement.create","quesqion.answered"};

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

        initData();
    }

    public void initData(){
        mBundle = getArguments();
        mCourseId = mBundle.getInt("course_id");
        newsCourseDataSource = new NewsCourseDataSource(SqliteChatUtil.getSqliteChatUtil(mContext, app.domain));
        List<NewsCourseEntity> newsCourseEntityList = getNewsCourseList(0);

        newsCourseEntityList = filterList(newsCourseEntityList);
        newsCourseEntityList = addLessonTitle(newsCourseEntityList);
        mAdapter = new StudyProcessRecyclerAdapter(mContext,newsCourseEntityList);
        studyProcessRecyclerView.setAdapter(mAdapter);
    }

    private List<NewsCourseEntity> getNewsCourseList(int start) {
        List<NewsCourseEntity> entities = newsCourseDataSource.getNewsCourses(start, Const.NEWS_LIMIT, mCourseId, app.loginUser.id);
        Collections.reverse(entities);
        return entities;
    }

    public List filterList(List<NewsCourseEntity> list){
        for (int i = 0;i<list.size();i++){
            NewsCourseEntity entity = list.get(i);
            if (!Arrays.asList(types).contains(entity.getBodyType())){
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    public List addLessonTitle(List<NewsCourseEntity> list){
        for (int i = 0;i<list.size();i++){
            NewsCourseEntity entity = list.get(i);
//            if (lessonIds.contains(entity.getObjectId()) || entity.getBodyType().equals("testpaper.reviewed") || entity.getBodyType().equals("announcement.create")){
            if (lessonIds.contains(entity.getObjectId()) || entity.getBodyType().equals("announcement.create")){
                continue;
            }else {
                NewsCourseEntity newsCourseEntity = new NewsCourseEntity();
                newsCourseEntity.setContent(entity.getContent());
                newsCourseEntity.setBodyType("course.lessonTitle");
                list.add(i,newsCourseEntity);
                lessonIds.add(entity.getObjectId());
                i++;
            }
        }
        return list;
    }

    @Override
    public MessageType[] getMsgTypes() {
        return super.getMsgTypes();
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
    }
}
