package com.edusoho.kuozhi.imserver.ui.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.edusoho.kuozhi.imserver.ui.broadcast.ResourceStatusReceiver;
import com.edusoho.kuozhi.imserver.ui.util.IResourceTask;
import com.edusoho.kuozhi.imserver.ui.util.ITaskStatusListener;

import java.util.HashMap;

/**
 * Created by suju on 16/8/28.
 */
public class MessageResourceHelper {

    private static final String TAG = "MessageResourceHelper";

    private Context mContext;
    private HashMap<Integer, IResourceTask> mTaskMap;

    public MessageResourceHelper(Context context) {
        this.mContext = context;
        mTaskMap = new HashMap<>();
    }

    public void addTask(IResourceTask task) {
        if (hasTask(task.getTaskId())) {
            Log.d(TAG, "task has:" + task.getTaskId());
            return;
        }
        Log.d(TAG, "add task:" + task.getTaskId());
        mTaskMap.put(task.getTaskId(), task);
        task.execute().addITaskStatusListener(createITaskStatusListener());
    }

    private ITaskStatusListener createITaskStatusListener() {
        return new ITaskStatusListener() {
            @Override
            public void onSuccess(int taskId, int taskType, String uri) {
                Intent intent = new Intent(ResourceStatusReceiver.ACTION);
                intent.putExtra(ResourceStatusReceiver.RES_ID, taskId);
                intent.putExtra(ResourceStatusReceiver.TASK_TYPE, taskType);
                intent.putExtra(ResourceStatusReceiver.RES_URI, uri);
                mContext.sendBroadcast(intent);
                removeTask(taskId);
            }

            @Override
            public void onFail(int taskId, int taskType) {
                Intent intent = new Intent(ResourceStatusReceiver.ACTION);
                intent.putExtra(ResourceStatusReceiver.RES_ID, taskId);
                mContext.sendBroadcast(intent);
                removeTask(taskId);
            }
        };
    }

    public void removeTask(int id) {
        IResourceTask task = mTaskMap.get(id);
        if (task == null) {
            return;
        }
        Log.d(TAG, "removeTask task:" + id);
        task.cancel();
        mTaskMap.remove(id);
    }

    public boolean hasTask(int id) {
        return mTaskMap.containsKey(id);
    }

    public void clear() {
        for (Integer id : mTaskMap.keySet()) {
            IResourceTask task = mTaskMap.get(id);
            if (task != null) {
                task.cancel();
            }
        }
        mTaskMap.clear();
        mTaskMap = null;
    }
}
