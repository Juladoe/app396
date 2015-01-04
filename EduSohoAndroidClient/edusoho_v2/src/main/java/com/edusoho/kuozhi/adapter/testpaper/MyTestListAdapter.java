package com.edusoho.kuozhi.adapter.testpaper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Testpaper.MyTestpaperData;
import com.edusoho.kuozhi.model.Testpaper.MyTestpaperResult;
import com.edusoho.kuozhi.model.Testpaper.Testpaper;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.testpaper.TestpaperResultFragment;
import com.edusoho.kuozhi.ui.lesson.TestpaperActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.kuozhi.view.EdusohoButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MyTestListAdapter extends ListBaseAdapter<MyTestpaperData> {

    protected ActionBarBaseActivity mActivity;
    private static final String TAG = "MyTestListAdapter";
    public ArrayList<MyTestpaperResult> myTestpaperResults;
    public HashMap<Integer, Testpaper> myTestpapers;
    public HashMap<Integer, Course> courses;

    private RedoClick redoClick;
    private DoClick doClick;
    private ShowClick showClick;

    public MyTestListAdapter(ActionBarBaseActivity activity, int resource) {
        super(activity, resource);
        mActivity = activity;

        redoClick = new RedoClick();
        doClick = new DoClick();
        showClick = new ShowClick();

        myTestpaperResults = new ArrayList<MyTestpaperResult>();
        myTestpapers = new HashMap<Integer, Testpaper>();
        courses = new HashMap<Integer, Course>();
    }


    @Override
    public void addItems(ArrayList<MyTestpaperData> list) {
    }

    @Override
    public void clear() {
        myTestpaperResults.clear();
        myTestpapers.clear();
        courses.clear();
    }

    private void listAddItem(MyTestpaperData data) {
        Log.d(null, "listAddItem->");
        myTestpaperResults.addAll(data.myTestpaperResults);
        myTestpapers.putAll(data.myTestpapers);
        courses.putAll(data.courses);
    }

    @Override
    public void addItem(MyTestpaperData data) {
        listAddItem(data);
        notifyDataSetChanged();
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
            view = inflater.inflate(mResource, null);
            holder = new ViewHolder();
            holder.mCourseTitle = (TextView) view.findViewById(R.id.testpaper_course_title);
            holder.mTestpaperName = (TextView) view.findViewById(R.id.testpaper_name);
            holder.mTeachersay = (TextView) view.findViewById(R.id.test_teacher_say);
            holder.mRedoBtn = (EdusohoButton) view.findViewById(R.id.testpaper_redo);
            holder.mShowBtn = (EdusohoButton) view.findViewById(R.id.testpaper_result);
            holder.mDoBtn = (EdusohoButton) view.findViewById(R.id.testpaper_do);
            holder.mStatusView = (TextView) view.findViewById(R.id.testpaper_status);
            holder.mTestpaperStartTime = (TextView) view.findViewById(R.id.testpaper_starttime);
            holder.mFullMark = (ESTextView) view.findViewById(R.id.testpaper_full_mark);
            holder.mVPartingLine = view.findViewById(R.id.verticle_parting_line);
            holder.mScore = (TextView) view.findViewById(R.id.my_score);

            holder.mCircle = (ImageView) view.findViewById(R.id.testpaper_icon);
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
        if (index == 0) {
            Log.d(this.TAG, holder.mFullMark.getVisibility() + "");
        }
        return view;
    }

    public class RedoClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = (Integer) view.getTag();
            doTestpaper(TestpaperActivity.REDO, index);
        }
    }

    public class ShowClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = (Integer) view.getTag();
            Log.d(null, "show--->");
            MyTestpaperResult testpaperResult = myTestpaperResults.get(index);

            Bundle bundle = new Bundle();
            bundle.putString(FragmentPageActivity.FRAGMENT, "TestpaperResultFragment");
            bundle.putString(Const.ACTIONBAR_TITLE, testpaperResult.paperName + " 考试结果");
            bundle.putInt(TestpaperResultFragment.RESULT_ID, testpaperResult.id);
            mActivity.app.mEngine.runNormalPluginWithBundle(
                    "FragmentPageActivity", mActivity, bundle);
        }
    }

    public class DoClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int index = (Integer) view.getTag();
//            doTestpaper(TestpaperActivity.DO,index);
            showTestpaper(TestpaperActivity.SHOW_TEST, index);
        }
    }

    private void showTestpaper(int type, int index) {
        MyTestpaperResult testpaperResult = myTestpaperResults.get(index);

        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAR_TITLE, testpaperResult.paperName);
        bundle.putInt(Const.mTestpaperResultId, testpaperResult.id);
        bundle.putInt(Const.LESSON_ID, getLessonId(testpaperResult.target));
        bundle.putInt(Const.TESTPAPER_DO_TYPE, type);
        bundle.putBoolean("isLoadTitleByNet", true);
        mActivity.app.mEngine.runNormalPluginWithBundle(
                "TestpaperActivity", mActivity, bundle);
    }

    private void doTestpaper(int type, int index) {
        MyTestpaperResult testpaperResult = myTestpaperResults.get(index);

        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAR_TITLE, testpaperResult.paperName);
        bundle.putInt(Const.MEDIA_ID, testpaperResult.testId);
        bundle.putInt(Const.LESSON_ID, getLessonId(testpaperResult.target));
        bundle.putInt(Const.TESTPAPER_DO_TYPE, type);
        bundle.putBoolean("isLoadTitleByNet", true);
        mActivity.app.mEngine.runNormalPluginWithBundle(
                "TestpaperActivity", mActivity, bundle);
        mActivity.finish();
    }

    private int getCourseId(String target) {
        int id = 0;
        if (target == null) {
            return 0;
        }
        String[] array = target.split("-");
        if (array != null && array.length > 0) {
            try {
                id = Integer.parseInt(array[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    private int getLessonId(String target) {
        int id = 0;
        String[] array = target.split("-");
        if (array != null && array.length > 0) {
            try {
                id = Integer.parseInt(array[2]);
            } catch (Exception e) {
                //
            }
        }

        return id;
    }

    public void invaliViewData(ViewHolder holder, int index) {
        MyTestpaperResult testpaperResult = myTestpaperResults.get(index);

        holder.mRedoBtn.setVisibility(View.GONE);
        holder.mShowBtn.setVisibility(View.GONE);
        holder.mDoBtn.setVisibility(View.GONE);
        holder.mStatusView.setVisibility(View.GONE);

        holder.mScore.setVisibility(View.GONE);
        holder.mVPartingLine.setVisibility(View.GONE);

        if (testpaperResult.teacherSay == null) {
            holder.mTeachersay.setVisibility(View.GONE);
        } else {
            holder.mTeachersay.setVisibility(View.VISIBLE);
            holder.mTeachersay.setText(String.format("评语：%s", testpaperResult.teacherSay));
        }
        holder.mTestpaperName.setText(testpaperResult.paperName);
        String startTime = testpaperResult.beginTime;
        holder.mTestpaperStartTime.setText(startTime.substring(0, 10));

        Testpaper testpaper = myTestpapers.get(testpaperResult.testId);
        if (testpaper == null) {
            holder.mTestpaperName.setText("该试卷已经删除");
            return;
        }
        holder.mFullMark.setText(String.format("满分：%s", (int) testpaper.score));
        Course course = courses.get(getCourseId(testpaper.target));
        holder.mCourseTitle.setText(String.format("来自课程:《%s》", course.title));

        String status = testpaperResult.status;
        if ("reviewing".equals(status)) {
            //显示查看结果
            holder.mStatusView.setText("正在批阅");
            holder.mShowBtn.setVisibility(View.VISIBLE);
            holder.mStatusView.setVisibility(View.VISIBLE);
            holder.mFullMark.setVisibility(View.GONE);

        } else if ("doing".equals(status)) {
            //显示继续考试
            holder.mStatusView.setText("未交卷");
            holder.mDoBtn.setVisibility(View.VISIBLE);
            holder.mStatusView.setVisibility(View.VISIBLE);
            holder.mFullMark.setVisibility(View.GONE);

        } else if ("finished".equals(status)) {
            //重考一次、查看结果
            holder.mScore.setText(String.format("%.1f", testpaperResult.score));
            holder.mRedoBtn.setVisibility(View.VISIBLE);
            holder.mVPartingLine.setVisibility(View.VISIBLE);
            holder.mShowBtn.setVisibility(View.VISIBLE);
            holder.mScore.setVisibility(View.VISIBLE);
            holder.mFullMark.setVisibility(View.VISIBLE);
        }
    }

    protected class ViewHolder {
        public ImageView mCircle;
        public TextView mCourseTitle;
        /**
         * 试卷状态
         */
        public TextView mStatusView;
        public TextView mTestpaperName;
        public TextView mTestpaperStartTime;
        public EdusohoButton mRedoBtn;
        public EdusohoButton mShowBtn;
        public EdusohoButton mDoBtn;
        public TextView mTeachersay;
        /**
         * 总分
         */
        public ESTextView mFullMark;
        public View mVPartingLine;
        /**
         * 得分
         */
        public TextView mScore;
    }

}
