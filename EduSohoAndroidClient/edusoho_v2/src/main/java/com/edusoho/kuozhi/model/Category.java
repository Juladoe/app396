package com.edusoho.kuozhi.model;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-14.
 */
public class Category {
    public int id;
    public String code;
    public String name;
    public String icon;
    public String path;
    public String weight;
    public int groupId;
    public int parentId;
    public String description;
    public int depth;
    public ArrayList<Category> childs;

    public static final String GROUP = "group";

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", path='" + path + '\'' +
                ", weight='" + weight + '\'' +
                ", groupId=" + groupId +
                ", parentId=" + parentId +
                ", description='" + description + '\'' +
                ", depth=" + depth +
                ", childs=" + childs +
                '}';
    }
}
