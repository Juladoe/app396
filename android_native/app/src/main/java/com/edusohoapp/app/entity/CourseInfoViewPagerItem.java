package com.edusohoapp.app.entity;

import android.view.View;

public class CourseInfoViewPagerItem<T> {
    public View pager;
    public T data;

    public void clear()
    {
        this.data = null;
    }
}
