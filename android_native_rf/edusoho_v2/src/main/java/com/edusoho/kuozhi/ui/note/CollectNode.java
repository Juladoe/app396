package com.edusoho.kuozhi.ui.note;

/**
 * Created by onewoman on 14-10-9.
 */
public class CollectNode {
    public String courseImage;
    public String courseName;
    public int total;
    public int courseId;

    public CollectNode(String courseImage, String courseName, int total,int courseId) {
        this.courseImage = courseImage;
        this.courseName = courseName;
        this.total = total;
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "CollectNode{" +
                "courseimage='" + courseImage + '\'' +
                ", coursename='" + courseName + '\'' +
                ", total=" + total +
                ", courseId=" + courseId +
                '}';
    }
}
