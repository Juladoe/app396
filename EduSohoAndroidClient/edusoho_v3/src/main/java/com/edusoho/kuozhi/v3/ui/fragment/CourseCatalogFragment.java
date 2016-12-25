package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.CourseCatalogueAdapter;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.CustomTitle;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.LessonProvider;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.LessonActivity;
import com.edusoho.kuozhi.v3.ui.LessonDownloadingActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.FixCourseListView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by DF on 2016/12/13.
 */
public class CourseCatalogFragment extends BaseFragment {

    private static final int NONE = 0;
    private static final int ISMEMBER = 1;
    private static final int VISITOR = 2;

    public boolean isJoin;
    public int mMemberStatus;
    public String mCourseId;
    public CustomTitle mCustomTitle;
    public CourseCatalogueAdapter mAdapter;
    private RelativeLayout mRlSpace;
    private FixCourseListView mLvCatalog;
    private CourseCatalogue mCourseCatalogue;
    private TextView tvSpace;
    private View view;
    private View mLoadView;
    private View mLessonEmpytView;
    private LoadDialog mProcessDialog;

    public CourseCatalogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_catalog, container, false);
        mCourseId = getArguments().getString("id");
        init();
        return view;
    }

    protected void init() {
        mRlSpace = (RelativeLayout) view.findViewById(R.id.rl_space);
        mLvCatalog = (FixCourseListView) view.findViewById(R.id.lv_catalog);
        mLoadView = view.findViewById(R.id.ll_frame_load);
        tvSpace = (TextView) view.findViewById(R.id.tv_space);
        mLessonEmpytView = view.findViewById(R.id.ll_course_catalog_empty);
        tvSpace.setOnClickListener(getCacheCourse());
        initCache();
    }

    protected void setLoadViewStatus(int visibility) {
        mLoadView.setVisibility(visibility);
    }

    private void initCatalogue() {
        setLoadViewStatus(View.VISIBLE);
        setLessonEmptyViewVisibility(View.GONE);
        RequestUrl requestUrl = app.bindNewUrl(Const.LESSON_CATALOG + "?courseId=" + mCourseId + "&token=" + app.token, true);
        requestUrl.heads.put("token", app.token);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCourseCatalogue = ((CourseActivity) getActivity()).parseJsonValue(response, new TypeToken<CourseCatalogue>() {
                });
                if (mCourseCatalogue.getLessons().size() != 0) {
                    initFirstLearnLesson();
                    initCustomChapterSetting();
                } else {
                    setLessonEmptyViewVisibility(View.VISIBLE);
                    setLoadViewStatus(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setLoadViewStatus(View.GONE);
            }
        });
    }

    private void initCustomChapterSetting() {
        RequestUrl requestUrl = app.bindNewUrl("/api/setting/course", false);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setLoadViewStatus(View.GONE);
                if (mMemberStatus == ISMEMBER && !TextUtils.isEmpty(app.token)) {
                    mRlSpace.setVisibility(View.VISIBLE);
                    initFirstLearnLesson();
                }
                CustomTitle cusotmTitle = new Gson().fromJson(response, CustomTitle.class);
                if (cusotmTitle != null && "1".equals(cusotmTitle.getCustomChapterEnable())) {
                    initLessonCatalog(cusotmTitle.getChapterName(), cusotmTitle.getPartName());
                }else {
                    initLessonCatalog(null,null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setLoadViewStatus(View.GONE);
            }
        });
    }

    private void setLessonEmptyViewVisibility(int visibility) {
        mLessonEmpytView.setVisibility(visibility);
    }

    public void initLessonCatalog(String chapter, String unit) {
        mAdapter = new CourseCatalogueAdapter(getActivity(), mCourseCatalogue, isJoin, chapter, unit);
        mLvCatalog.setAdapter(mAdapter);
        mLvCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ("chapter".equals(mCourseCatalogue.getLessons().get(position).getType()) || "unit".equals(mCourseCatalogue.getLessons().get(position).getType())) {
                    return;
                }
                if (mAdapter.isSelected(position)) {
                    return;
                }
                if (TextUtils.isEmpty(app.token)) {
                    CoreEngine.create(getContext()).runNormalPlugin("LoginActivity", getContext(), null);
                    return;
                }
                if (mMemberStatus != ISMEMBER && "0".equals(mCourseCatalogue.getLessons().get(position).getFree())) {
                    CommonUtil.shortCenterToast(getActivity(), getString(R.string.unjoin_course_hint));
                    return;
                }
                perpareStartLearnLesson(position);
            }
        });
        mLvCatalog.setOnTouchListener(new View.OnTouchListener() {

            private int downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        downX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(((int) event.getX()) - downX) > 0) {
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }

    private void initFirstLearnLesson() {
        if (mCourseCatalogue != null) {
            List<CourseCatalogue.LessonsBean> lessonsBeanList = mCourseCatalogue.getLessons();
            if (lessonsBeanList == null || lessonsBeanList.isEmpty()) {
                return;
            }
            final Bundle bundle = new Bundle();
            CourseCatalogue.LessonsBean lessonsBean = null;
            Map<String, String> learnStatuses = mCourseCatalogue.getLearnStatuses();
            //没加入
            if (mMemberStatus != ISMEMBER) {
                lessonsBean = findFreeLessonInList();
                bundle.putString(Const.COURSE_CHANGE_STATE, Const.COURSE_CHANGE_STATE_NONE);
            } else if (learnStatuses.containsKey("-1")) {
                //还没开始学,学第一个
                lessonsBean = findFirstLessonInList();
                bundle.putString(Const.COURSE_CHANGE_STATE, Const.COURSE_CHANGE_STATE_NONE);
            } else {
                lessonsBean = findFirseLearnLessonWithStatus(mCourseCatalogue);
                bundle.putString(Const.COURSE_CHANGE_STATE, Const.COURSE_CHANGE_STATE_STARTED);
            }

            if (lessonsBean == null) {
                return;
            }

            new LessonProvider(getContext()).getLesson(AppUtil.parseInt(lessonsBean.getId()))
                    .success(new NormalCallback<LessonItem>() {
                        @Override
                        public void success(LessonItem lessonItem) {
                            bundle.putSerializable(Const.COURSE_CHANGE_OBJECT, lessonItem);
                            MessageEngine.getInstance().sendMsg(Const.COURSE_HASTRIAL, bundle);
                        }
                    });
        }
    }

    private CourseCatalogue.LessonsBean findFirseLearnLessonWithStatus(CourseCatalogue courseCatalogue) {
        Map<String, String> learnStatuses = courseCatalogue.getLearnStatuses();
        List<CourseCatalogue.LessonsBean> lessonsBeanList = courseCatalogue.getLessons();

        int size = lessonsBeanList.size();
        CourseCatalogue.LessonsBean currentFinishedLessonsBean = null;
        for (int i = 0; i < size; i++) {
            CourseCatalogue.LessonsBean lessonsBean = lessonsBeanList.get(i);
            String status = learnStatuses.get(lessonsBean.getId());
            if ("learning".equals(status)) {
                return lessonsBean;
            }
            if ("finished".equals(status)) {
                currentFinishedLessonsBean = lessonsBean;
            }
        }

        return currentFinishedLessonsBean;
    }

    private CourseCatalogue.LessonsBean findFirstLessonInList() {
        List<CourseCatalogue.LessonsBean> lessonsBeanList = mCourseCatalogue.getLessons();
        for (int i = 0; i < lessonsBeanList.size(); i++) {
            CourseCatalogue.LessonsBean lessonsBean = lessonsBeanList.get(i);
            if ("lesson".equals(lessonsBean.getItemType())) {
                return lessonsBean;
            }
        }

        return null;
    }

    private CourseCatalogue.LessonsBean findFreeLessonInList() {
        List<CourseCatalogue.LessonsBean> lessonsBeanList = mCourseCatalogue.getLessons();
        for (int i = 0; i < lessonsBeanList.size(); i++) {
            CourseCatalogue.LessonsBean lessonsBean = lessonsBeanList.get(i);
            if ("lesson".equals(lessonsBean.getItemType()) && "1".equals(lessonsBean.getFree())) {
                return lessonsBean;
            }
        }
        return null;
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(getActivity());
        }
        mProcessDialog.show();
    }

    protected void hideProcesDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    private void sendMessageToCourse(LessonItem lessonItem) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.COURSE_CHANGE_OBJECT, lessonItem);
        MessageEngine.getInstance().sendMsg(Const.COURSE_CHANGE, bundle);

        bundle.putString(Const.COURSE_CHANGE_STATE, Const.COURSE_CHANGE_STATE_STARTED);
        MessageEngine.getInstance().sendMsg(Const.COURSE_HASTRIAL, bundle);
    }

    private void getFullLessonFromServer(CourseCatalogue.LessonsBean lessonsBean) {
        showProcessDialog();
        new LessonProvider(getContext()).getLesson(AppUtil.parseInt(lessonsBean.getId()))
                .success(new NormalCallback<LessonItem>() {
                    @Override
                    public void success(LessonItem lessonItem) {
                        hideProcesDialog();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Const.COURSE_CHANGE_OBJECT, lessonItem);
                        MessageEngine.getInstance().sendMsg(Const.COURSE_CHANGE, bundle);

                        bundle.putString(Const.COURSE_CHANGE_STATE, Const.COURSE_CHANGE_STATE_STARTED);
                        MessageEngine.getInstance().sendMsg(Const.COURSE_HASTRIAL, bundle);
                    }
                }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                hideProcesDialog();
            }
        });
    }

    public void perpareStartLearnLesson(int position) {
        CourseCatalogue.LessonsBean lessonsBean = mCourseCatalogue.getLessons().get(position);
        if ("self".equals(lessonsBean.getMediaSource())) {
            if ("audio".equals(lessonsBean.getType()) || "video".equals(lessonsBean.getType())) {
                getFullLessonFromServer(lessonsBean);
                return;
            }
        }
        sendMessageToCourse(lessonsBean.toLessonItem());
    }

    public void startLessonActivity(int lessonId, int courseId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Const.LESSON_ID, lessonId);
        bundle.putInt(Const.COURSE_ID, courseId);
        bundle.putIntegerArrayList(Const.LESSON_IDS, getLessonArray());
        CoreEngine.create(getContext()).runNormalPluginWithBundleForResult(
                "LessonActivity", getActivity(), bundle, LessonActivity.REQUEST_LEARN);
    }

    public ArrayList<Integer> getLessonArray() {
        ArrayList<Integer> lessonArray = new ArrayList<>();
        for (CourseCatalogue.LessonsBean lessonsBean : mCourseCatalogue.getLessons()) {
            if ("lesson".equals(lessonsBean.getItemType())) {
                lessonArray.add(Integer.parseInt(lessonsBean.getId()));
            }
        }
        return lessonArray;
    }

    /**
     * 外部刷新数据
     */
    public void reFreshView(boolean mIsJoin) {
        mMemberStatus = mIsJoin ? ISMEMBER : VISITOR;
        isJoin = mIsJoin;
        initCatalogue();
    }

    /**
     * 获取手机可用空间,该界面要先判断是否显示rlSpace
     */
    private void initCache() {
        TextView tvSpace = (TextView) view.findViewById(R.id.tv_space);
        TextView tvCourse = (TextView) view.findViewById(R.id.tv_course);
        tvSpace.setText(getString(R.string.course_catalog_space) + getRomAvailableSize());
        tvCourse.setOnClickListener(getCacheCourse());
    }

    public View.OnClickListener getCacheCourse() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getRomAvailableSize().contains("M")) {
                    if (Float.parseFloat(getRomAvailableSize().replaceAll("[a-zA-Z]", "").trim()) < 100) {
                        CommonUtil.shortCenterToast(getActivity(), getString(R.string.cache_hint));
                        return;
                    }
                }
                startActivity(new Intent(getContext(), LessonDownloadingActivity.class).putExtra(Const.COURSE_ID, Integer.parseInt(mCourseId)));
            }
        };
    }

    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(getActivity(), blockSize * availableBlocks).replace("B", "");
    }
}