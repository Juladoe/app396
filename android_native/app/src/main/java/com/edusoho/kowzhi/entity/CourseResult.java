package com.edusoho.kowzhi.entity;

import java.util.Arrays;
import java.util.HashMap;

public class CourseResult {

	public CourseItem[] courses;
	public HashMap<String, UserItem> users;
	public String mode;
	public int page;
	public int total_page;

	@Override
	public String toString() {
		return "CourseResult [courses=" + Arrays.toString(courses) + ", users="
				+ users + ", mode=" + mode + ", page=" + page + ", count="
				+ total_page + "]";
	}

}
