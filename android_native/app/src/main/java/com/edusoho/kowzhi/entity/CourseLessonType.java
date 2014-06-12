package com.edusoho.kowzhi.entity;

public enum CourseLessonType {
	
	VIDEO, TEXT, TESTPAPER, AUDIO, EMPTY;
	
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
