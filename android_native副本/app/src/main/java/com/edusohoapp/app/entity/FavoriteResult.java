package com.edusohoapp.app.entity;

import java.util.Arrays;
import java.util.HashMap;

public class FavoriteResult{
	public String status;
    public CourseItem[] favoriteCourses;
    public HashMap<String, UserItem> users;
    public int page;
    public int total_page;
}
