package com.edusoho.kuozhi.v3.util;

import com.edusoho.kuozhi.v3.entity.discovery.DiscoveryColumn;
import java.util.ArrayList;
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
                    if (discoveryColumn != null && discoveryColumn.data != null && !discoveryColumn.data.isEmpty()) {
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