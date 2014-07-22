package com.edusoho.kuozhi.model;

/**
 * Created by howzhi on 14-7-18.
 */
public class CourseMenu {
    public String name;
    public String type;
    public String parentId;

    public CourseMenu(String type, String name, String parentId)
    {
        this.parentId = parentId;
        this.name = name;
        this.type = type;
    }
}
