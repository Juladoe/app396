package com.edusoho.kuozhi.entity;

import java.util.HashMap;

public class CourseInfoItem {
	public CourseItem couse_introduction;
	public CourseLessonItem[] course_list;
	public CourseCommentItem[] course_comment;
	public HashMap<String, UserItem> users;
	public HashMap<String, UserItem> teacherUsers;
	public Member member;
	public HashMap<String, String> learnStatuses;
    public boolean favoriteStatus;
    public boolean isStudent;
}
