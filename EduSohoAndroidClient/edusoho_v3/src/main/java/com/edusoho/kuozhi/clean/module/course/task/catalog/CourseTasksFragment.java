package com.edusoho.kuozhi.clean.module.course.task.catalog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.CourseItem;
import com.edusoho.kuozhi.clean.bean.CourseLearningProgress;
import com.edusoho.kuozhi.clean.bean.CourseProject;
import com.edusoho.kuozhi.clean.bean.CourseTask;
import com.edusoho.kuozhi.clean.bean.MessageEvent;
import com.edusoho.kuozhi.clean.module.base.BaseFragment;
import com.edusoho.kuozhi.clean.module.course.CourseProjectActivity;
import com.edusoho.kuozhi.clean.module.course.CourseProjectFragmentListener;
import com.edusoho.kuozhi.clean.module.course.dialog.LearnCourseProgressDialog;
import com.edusoho.kuozhi.clean.module.course.dialog.TaskFinishDialog;
import com.edusoho.kuozhi.clean.module.course.task.menu.question.QuestionActivity;
import com.edusoho.kuozhi.clean.module.course.task.menu.info.CourseMenuInfoFragment;
import com.edusoho.kuozhi.clean.module.course.task.menu.rate.RatesActivity;
import com.edusoho.kuozhi.clean.utils.ItemClickSupport;
import com.edusoho.kuozhi.clean.widget.CourseMenuButton;
import com.edusoho.kuozhi.clean.widget.ESIconView;
import com.edusoho.kuozhi.clean.widget.ESProgressBar;
import com.edusoho.kuozhi.clean.widget.FragmentPageActivity;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.NewsCourseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by JesseHuang on 2017/3/26.
 */

public class CourseTasksFragment extends BaseFragment<CourseTasksContract.Presenter> implements
        CourseTasksContract.View, CourseProjectFragmentListener, View.OnClickListener {

    private static final String COURSE_PROJECT_MODEL = "CourseProjectModel";
    private CourseTasksContract.Presenter mPresenter;
    private RecyclerView mTaskRecyclerView;
    private FloatingActionButton mMenuButton;
    private TextView mMenuClose;
    private View mCourseMenuLayout;
    private View mCourseProgressBar;
    private CourseMenuButton mCourseInfo;
    private ESProgressBar mLearnProgressRate;
    private ESIconView mCourseProgressInfo;

    private CourseProject mCourseProject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCourseProject = (CourseProject) bundle.getSerializable(COURSE_PROJECT_MODEL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_tasks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTaskRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content);
        mMenuButton = (FloatingActionButton) view.findViewById(R.id.floating_button);
        mCourseInfo = (CourseMenuButton) view.findViewById(R.id.btn_course_menu_info);
        mLearnProgressRate = (ESProgressBar) view.findViewById(R.id.pb_learn_progress);
        mCourseProgressBar = view.findViewById(R.id.layout_progress);
        mCourseProgressInfo = (ESIconView) view.findViewById(R.id.icon_progress_info);

        mMenuClose = (TextView) view.findViewById(R.id.tv_close_menu);
        mCourseMenuLayout = view.findViewById(R.id.bottom_menu_layout);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(mCourseMenuLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomMenu(behavior);
                //TaskFinishDialog.newInstance().show(getActivity().getSupportFragmentManager(), "TaskFinishDialog");
            }
        });

        mMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomMenu(behavior);
            }
        });

        mPresenter = new CourseTasksPresenter(this, mCourseProject, isJoin());
        mPresenter.subscribe();
        view.findViewById(R.id.btn_course_menu_question).setOnClickListener(this);
        view.findViewById(R.id.btn_course_menu_rate).setOnClickListener(this);
        view.findViewById(R.id.btn_course_menu_discuss).setOnClickListener(this);
    }

    protected void launchDiscussActivity() {
        CoreEngine.create(getContext()).runNormalPlugin("NewsCourseActivity", getContext(), new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(NewsCourseActivity.COURSE_ID, mCourseProject.id);
                startIntent.putExtra(NewsCourseActivity.SHOW_TYPE, NewsCourseActivity.DISCUSS_TYPE);
                startIntent.putExtra(NewsCourseActivity.FROM_NAME, mCourseProject.title);
            }
        });
    }

    private boolean isJoin() {
        if (getActivity() != null && getActivity() instanceof CourseProjectActivity) {
            return ((CourseProjectActivity) getActivity()).isJoin();
        }
        return false;
    }

    @Override
    public void showCourseMenuButton(boolean show) {
        mMenuButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showBottomMenu(BottomSheetBehavior behavior) {
        if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mMenuButton.setVisibility(View.GONE);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            mMenuButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenuButton.setVisibility(View.VISIBLE);
                }
            }, 250);
        }
    }

    @Override
    public String getBundleKey() {
        return COURSE_PROJECT_MODEL;
    }

    @Override
    public void showCourseTasks(List<CourseItem> taskItems, boolean isJoin) {
        final CourseTaskAdapter adapter = new CourseTaskAdapter(getActivity(), taskItems,
                CourseProject.LearnMode.getMode(mCourseProject.learnMode), isJoin);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTaskRecyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(mTaskRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                int viewType = adapter.getItemViewType(position);
                if (viewType == CourseItemEnum.CHAPTER.getIndex() || viewType == CourseItemEnum.UNIT.getIndex()) {
                    return;
                }
                CourseItem item = adapter.getItem(position);
                if (item.task.lock) {
                    return;
                }
                TaskTypeEnum type = TaskTypeEnum.fromString(item.task.type);
                switch (type) {
                    case DOWNLOAD:
                        showToast(getString(R.string.donwload_task_not_support));
                        break;
                    case DISCUSS:
                        showToast(getString(R.string.discuss_task_not_support));
                        break;
                    case FLASH:
                        showToast(getString(R.string.flash_task_not_support));
                        break;
                    default:
                        adapter.switchClickPosition(v, item);
                        EventBus.getDefault().post(new MessageEvent<>(item.task
                                , MessageEvent.LEARN_TASK));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        ((CourseProjectActivity) getActivity()).stopAudio();
        if (v.getId() == R.id.btn_course_menu_discuss) {
            launchDiscussActivity();
        } else if (v.getId() == R.id.btn_course_menu_rate) {
            RatesActivity.launch(getContext(), mCourseProject);
        } else if (v.getId() == R.id.btn_course_menu_question) {
            QuestionActivity.launch(getContext(), mCourseProject.id);
        }
    }

    @Override
    public void showNextTaskOnCover(CourseTask task, boolean isFirstTask) {
        SparseArray<Object> data = new SparseArray<>();
        data.put(0, task);
        data.put(1, isFirstTask);
        MessageEvent<SparseArray<Object>> progressMsg = new MessageEvent<>(data, MessageEvent.SHOW_NEXT_TASK);
        EventBus.getDefault().post(progressMsg);
    }

    @Override
    public void showLearnProgress(final CourseLearningProgress progress) {
        mCourseProgressBar.setVisibility(View.VISIBLE);
        mLearnProgressRate.setProgress(Math.round(progress.progress));

        mCourseProgressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LearnCourseProgressDialog.newInstance(progress, mCourseProject)
                        .show(getActivity().getSupportFragmentManager(), "LearnCourseProgressDialog");
            }
        });

        mCourseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CourseProjectActivity) getActivity()).stopAudio();
                Bundle bundle = new Bundle();
                bundle.putSerializable(CourseMenuInfoFragment.COURSE_PROJECT_MODEL, mCourseProject);
                bundle.putSerializable(CourseMenuInfoFragment.COURSE_PROGRESS, progress);
                FragmentPageActivity.launchFragmentPageActivity(getActivity(), CourseMenuInfoFragment.class.getName(), bundle);
            }
        });
    }

    @Override
    public void onReceiveMessage(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case MessageEvent.COURSE_EXIT:
                mCourseProgressBar.setVisibility(View.GONE);
                break;
            case MessageEvent.FINISH_TASK:
                ((CourseTaskAdapter) mTaskRecyclerView.getAdapter()).finishTask((int) messageEvent.getMessageBody());
                break;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onFinishTask(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case MessageEvent.FINISH_TASK:
                ((CourseTaskAdapter) mTaskRecyclerView.getAdapter()).finishTask((int) messageEvent.getMessageBody());
                break;
        }
    }
}
