package com.edusoho.kuozhi.v3.entity.course;

import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;

import java.io.Serializable;
import java.util.List;

/**
 * Created by remilia on 2017/1/9.
 */
public class LearningCourse implements Serializable {
    private int start;
    private int limit;
    private String total;
    private List<Course> data;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Course> getData() {
        return data;
    }

    public void setData(List<Course> data) {
        this.data = data;
    }
}
