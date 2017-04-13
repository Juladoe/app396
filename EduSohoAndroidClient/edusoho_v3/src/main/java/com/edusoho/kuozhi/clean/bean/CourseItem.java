package com.edusoho.kuozhi.clean.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/4/6.
 */

public class CourseItem implements Serializable {
    public int id;
    public String courseId;
    public String type;
    public String parentId;
    public int number;
    public int seq;
    public String title;
    public String createdTime;
    public List<CourseTask> tasks;

    public List<TaskItem> toTaskItems() {
        List<TaskItem> taskItems = new ArrayList<>();
        if (tasks == null) {
            TaskItem taskItem = new TaskItem();
            taskItem.id = id;
            taskItem.type = type;
            taskItem.number = number;
            taskItem.title = title;
            taskItems.add(taskItem);
        } else if (tasks.size() == 1) {
            CourseTask courseTask = tasks.get(0);
            TaskItem taskItem = new TaskItem();
            taskItem.id = courseTask.id;
            taskItem.title = courseTask.title;
            taskItem.isFree = courseTask.isFree;
            taskItem.type = courseTask.type;
            taskItem.number = courseTask.number;
            taskItem.secondNumber = 0;
            taskItem.length = courseTask.length;
            taskItems.add(taskItem);
        } else {
            int index = 1;
            for (CourseTask courseTask : tasks) {
                TaskItem taskItem = new TaskItem();
                taskItem.id = courseTask.id;
                taskItem.title = courseTask.title;
                taskItem.isFree = courseTask.isFree;
                taskItem.type = courseTask.type;
                taskItem.number = courseTask.number;
                taskItem.secondNumber = index++;
                taskItem.length = courseTask.length;
                taskItems.add(taskItem);
            }
        }
        return taskItems;
    }
}
