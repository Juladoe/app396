package com.edusoho.kuozhi.adapter.testpaper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.EdusohoBaseAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.BaseResult;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.model.Testpaper.MyTestpaperData;
import com.edusoho.kuozhi.model.Testpaper.MyTestpaperResult;
import com.edusoho.kuozhi.model.Testpaper.Testpaper;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperResultFragment;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoButton;

import java.util.ArrayList;
import java.util.HashMap;

public class TestpaperListAdapter extends EdusohoBaseAdapter {

    protected LayoutInflater inflater;
    protected int mResouce;
    protected ActionBarBaseActivity mActivity;
    public ArrayList<MyTestpaperResult> myTestpaperResults;
    public HashMap<Integer, Testpaper> myTestpapers;
    public HashMap<Integer, Course> courses;

    private RedoClick redoClick;
    private DoClick doClick;
    private ShowClick showClick;

    public TestpaperListAdapter(ActionBarBaseActivity activity, MyTestpaperData data,
                                int resource) {
        mActivity = activity;
        mResouce = resource;
        inflater = LayoutInflater.from(mActivity);

        redoClick = new RedoClick();
        doClick = new DoClick();
        showClick = new ShowClick();

        myTestpaperResults = new ArrayList<MyTestpaperResult>();
        myTestpapers = new HashMap<Integer, Testpaper>();
        courses = new HashMap<Integer, Course>();
        listAddItem(data);
    }

    private void listAddItem(MyTestpaperData data)
    {
        Log.d(null, "listAddItem->");
        myTestpaperResults.addAll(data.myTestpaperResults);
        myTestpapers.putAll(data.myTestpapers);
        courses.putAll(data.courses);
    }

    public void addItem(MyTestpaperData data)
    {
        listAddItem(data);
        notifyDataSetChanged();
    }

    public void setItems(CourseResult courseResult){
    }

    @Override
    public int getCount() {
        return myTestpaperResults.size();
    }

    @Override
    public Object getItem(int index) {
        return myTestpaperResults.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int index, View view, ViewGroup vg) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(mResouce, null);
            holder = new ViewHolder();
            holder.mCourseTitle = (TextView) view.findViewById(R.id.testpaper_course_title);
            holder.mTestpaperName = (TextView) view.findViewById(R.id.testpaper_name);
            holder.mRedoBtn = (EdusohoButton) view.findViewById(R.id.testpaper_redo);
            holder.mShowBtn = (EdusohoButton) view.findViewById(R.id.testpaper_result);
            holder.mDoBtn = (EdusohoButton) view.findViewById(R.id.testpaper_do);
            holder.mStatusView = (TextView) view.findViewById(R.id.testpaper_status);
            holder.mTestpaperStartTime = (TextView) view.findViewById(R.id.testpaper_starttime);

            holder.aq = new AQuery(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        invaliViewData(holder, index);

        holder.mRedoBtn.setTag(index);
        holder.mDoBtn.setTag(index);
        holder.mShowBtn.setTag(index);

        holder.mRedoBtn.setOnClickListener(redoClick);
        holder.mDoBtn.setOnClickListener(doClick);
        holder.mShowBtn.setOnClickListener(showClick);
        return view;
    }

    public class RedoClick implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            int index = (Integer) view.getTag();
            doTestpaper(TestpaperActivity.REDO, index);
        }
    }

    public class ShowClick implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            int index = (Integer) view.getTag();
            MyTestpaperResult testpaperResult =  myTestpaperResults.get(index);

            Bundle bundle = new Bundle();
            bundle.putString(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
            bundle.putString(Const.ACTIONBAT_TITLE, testpaperResult.paperName + " 考试结果");
            bundle.putInt(TestpaperResultFragment.RESULT_ID, testpaperResult.id);
            mActivity.app.mEngine.runNormalPluginWithBundle(
                    "FragmentPageActivity", mActivity, bundle);
        }
    }

    public class DoClick implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            int index = (Integer) view.getTag();
            showTestpaper(TestpaperActivity.SHOW_TEST, index);
        }
    }

    private void showTestpaper(int type, int index)
    {
        MyTestpaperResult testpaperResult =  myTestpaperResults.get(index);

        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAT_TITLE, testpaperResult.paperName);
        bundle.putInt(Const.mTestpaperResultId, testpaperResult.id);
        bundle.putInt(Const.LESSON_ID, getLessonId(testpaperResult.target));
        bundle.putInt(Const.TESTPAPER_DO_TYPE, type);
        bundle.putBoolean("isLoadTitleByNet", true);
        mActivity.app.mEngine.runNormalPluginWithBundle(
                "TestpaperActivity", mActivity, bundle);
    }

    private void doTestpaper(int type, int index)
    {
        MyTestpaperResult testpaperResult =  myTestpaperResults.get(index);

        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAT_TITLE, testpaperResult.paperName);
        bundle.putInt(Const.MEDIA_ID, testpaperResult.testId);
        bundle.putInt(Const.LESSON_ID, getLessonId(testpaperResult.target));
        bundle.putInt(Const.TESTPAPER_DO_TYPE, type);
        bundle.putBoolean("isLoadTitleByNet", true);
        mActivity.app.mEngine.runNormalPluginWithBundle(
                "TestpaperActivity", mActivity, bundle);
        mActivity.finish();
    }

    private int getCourseId(String target)
    {
        int id = 0;
        if (target == null) {
            return 0;
        }
        String[] array = target.split("-");
        if (array != null && array.length > 0) {
            try {
                id = Integer.parseInt(array[1]);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    private int getLessonId(String target)
    {
        int id = 0;
        String[] array = target.split("-");
        if (array != null && array.length > 0) {
            try {
                id = Integer.parseInt(array[2]);
            }catch (Exception e) {
                //
            }
        }

        return id;
    }

    public void invaliViewData(ViewHolder holder, int index)
    {
        MyTestpaperResult testpaperResult =  myTestpaperResults.get(index);

        holder.mRedoBtn.setVisibility(View.GONE);
        holder.mShowBtn.setVisibility(View.GONE);
        holder.mDoBtn.setVisibility(View.GONE);
        holder.mStatusView.setVisibility(View.GONE);

        holder.mTestpaperName.setText(testpaperResult.paperName);
        holder.mTestpaperStartTime.setText(testpaperResult.beginTime);

        Testpaper testpaper = myTestpapers.get(testpaperResult.testId);
        if (testpaper == null) {
            holder.mCourseTitle.setText("该试卷已经删除");
            return;
        }
        Course course = courses.get(getCourseId(testpaper.target));
        holder.mCourseTitle.setText(course.title);
        int width = (int)(EdusohoApp.app.screenW * 0.4f);
        if (TextUtils.isEmpty(course.largePicture)) {
            holder.aq.id(R.id.testpaper_icon).image(R.drawable.noram_course);
        } else {
            holder.aq.id(R.id.testpaper_icon).image(
                    course.largePicture, false, true, width, R.drawable.noram_course);
            holder.aq.id(R.id.testpaper_icon)
                    .width(width, false)
                    .height(AppUtil.getCourseListCoverHeight(width), false);
        }

        String status = testpaperResult.status;

        if ("reviewing".equals(status)) {
            holder.mStatusView.setText("正在批阅");
            holder.mStatusView.setBackgroundDrawable(
                    mActivity.getResources().getDrawable(R.drawable.red_card_bg));
            holder.mShowBtn.setVisibility(View.VISIBLE);
        } else if ("doing".equals(status)) {
            holder.mStatusView.setText("未交卷");
            holder.mStatusView.setBackgroundDrawable(
                    mActivity.getResources().getDrawable(R.drawable.red_card_bg));
            holder.mDoBtn.setVisibility(View.VISIBLE);
        } else if ("finished".equals(status)) {
            holder.mStatusView.setText(String.format("得分:%.1f", testpaperResult.score));
            holder.mStatusView.setBackgroundDrawable(
                    mActivity.getResources().getDrawable(R.drawable.blue_card_bg));
            holder.mRedoBtn.setVisibility(View.VISIBLE);
            holder.mShowBtn.setVisibility(View.VISIBLE);
        }
    }

    protected class ViewHolder {
        public AQuery aq;
        public TextView mCourseTitle;
        public TextView mStatusView;
        public TextView mTestpaperName;
        public TextView mTestpaperStartTime;
        public EdusohoButton mRedoBtn;
        public EdusohoButton mShowBtn;
        public EdusohoButton mDoBtn;
    }

}
