package com.edusoho.kuozhi.entity;

public enum CourseLessonType {
	
	VIDEO, TEXT, TESTPAPER, AUDIO, PPT, EMPTY;
	
	public static CourseLessonType value(String typeName)
	{
		CourseLessonType type;
		try {
			type =  valueOf(typeName.toUpperCase());
		}catch (Exception e) {
			type = EMPTY;
		}
		return type;
	}
}
