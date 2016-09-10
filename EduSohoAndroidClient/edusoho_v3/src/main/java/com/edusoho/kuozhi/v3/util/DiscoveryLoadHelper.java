package com.edusoho.kuozhi.v3.util;

import com.baidu.cyberplayer.utils.A;
import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryClassroom;
import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryColumn;
import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryCourse;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.discovery.DiscoveryModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by suju on 16/9/10.
 */
public class DiscoveryLoadHelper
{
    private int mTaskCount;
    private List<DiscoveryColumn> mDiscoveryCardPropertieList;
    private List<DiscoveryLoadTask> mTaskList;
    private ResultCallback mResultCallback;

    public DiscoveryLoadHelper() {
        mTaskList = new ArrayList<>();
        mDiscoveryCardPropertieList = new ArrayList<>();
    }

    public void addTask(DiscoveryColumn discoveryColumn) {
        mTaskList.add(new DiscoveryLoadTask(discoveryColumn));
        mTaskCount = mTaskList.size();
    }

    public void invoke(ResultCallback resultCallback) {
        this.mResultCallback = resultCallback;
        for (DiscoveryLoadTask loadTask : mTaskList) {
            loadTask.exectue(new DiscoveryLoadTask.TaskCallback() {
                @Override
                public void onResult(DiscoveryColumn discoveryColumn) {
                    if (discoveryColumn != null) {
                        mDiscoveryCardPropertieList.add(discoveryColumn);
                    }
                    mTaskCount --;
                    if (mTaskCount <= 0) {
                        mResultCallback.onResult(mDiscoveryCardPropertieList);
                    }
                }
            });
        }
    }

    public interface ResultCallback {
        void onResult(List<DiscoveryColumn> discoveryCardProperties);
    }
}

class DiscoveryLoadTask {

    private DiscoveryModel mDiscoveryModel;
    private DiscoveryColumn mDiscoveryColumn;
    private TaskCallback mTaskCallback;

    public DiscoveryLoadTask(DiscoveryColumn discoveryColumn) {
        this.mDiscoveryColumn = discoveryColumn;
        this.mDiscoveryModel = new DiscoveryModel();
    }

    public void exectue(TaskCallback taskCallback) {
        this.mTaskCallback = taskCallback;
        doInBackground();
    }

    protected void doInBackground() {
        if ("course".equals(mDiscoveryColumn.type) || "live".equals(mDiscoveryColumn.type)) {
            getDiscoveryCourseByColumn();
        } else if ("classroom".equals(mDiscoveryColumn.type)) {
            getDiscoveryClassroomByColumn();
        } else {
            getDiscoveryEmptyColumns();
        }
    }


    public void onResult(DiscoveryColumn discoveryColumn) {
        if (mTaskCallback != null) {
            mTaskCallback.onResult(discoveryColumn);
        }
    }

    private void getDiscoveryClassroomByColumn() {
        mDiscoveryModel.getDiscoveryClassroomByColumn(mDiscoveryColumn, new ResponseCallbackListener<List<DiscoveryClassroom>>() {
            @Override
            public void onSuccess(List<DiscoveryClassroom> discoveryClassroomList) {
                if (discoveryClassroomList != null && discoveryClassroomList.size() > 0) {
                    if (discoveryClassroomList.size() % 2 != 0) {
                        discoveryClassroomList.add(new DiscoveryClassroom(true));
                    }
                    mDiscoveryColumn.data = discoveryClassroomList;
                    onResult(mDiscoveryColumn);
                }
            }

            @Override
            public void onFailure(String code, String message) {
                onResult(null);
            }
        });
    }

    private void getDiscoveryEmptyColumns() {
        mDiscoveryModel.getDiscoveryEmptyColumns(new ResponseCallbackListener<List<DiscoveryCourse>>() {
            @Override
            public void onSuccess(List<DiscoveryCourse> discoveryCourseList) {
                if (discoveryCourseList != null && discoveryCourseList.size() > 0) {
                    filterCoursesInClassroom(discoveryCourseList);
                    if (discoveryCourseList.size() <= 0) {
                        return;
                    }
                    if (discoveryCourseList.size() % 2 != 0) {
                        discoveryCourseList.add(new DiscoveryCourse(true));
                    }

                    mDiscoveryColumn.data = discoveryCourseList;
                    onResult(mDiscoveryColumn);
                }
            }

            @Override
            public void onFailure(String code, String message) {
                onResult(null);
            }
        });
    }

    private void getDiscoveryCourseByColumn() {
        mDiscoveryModel.getDiscoveryCourseByColumn(mDiscoveryColumn, new ResponseCallbackListener<List<DiscoveryCourse>>() {
            @Override
            public void onSuccess(List<DiscoveryCourse> discoveryCourseList) {
                if (discoveryCourseList != null && discoveryCourseList.size() > 0) {
                    filterCoursesInClassroom(discoveryCourseList);
                    if (discoveryCourseList.size() <= 0) {
                        return;
                    }
                    if (discoveryCourseList.size() % 2 != 0) {
                        discoveryCourseList.add(new DiscoveryCourse(true));
                    }
                    mDiscoveryColumn.data = discoveryCourseList;
                    onResult(mDiscoveryColumn);
                }
            }

            @Override
            public void onFailure(String code, String message) {
                onResult(null);
            }
        });
    }

    private void filterCoursesInClassroom(List<DiscoveryCourse> list) {
        Iterator<DiscoveryCourse> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().parentId != 0) {
                iterator.remove();
            }
        }
    }

    interface TaskCallback {
        void onResult(DiscoveryColumn discoveryColumn);
    }
}
