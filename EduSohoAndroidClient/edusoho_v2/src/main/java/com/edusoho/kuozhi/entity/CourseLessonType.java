package com.edusoho.kuozhi.entity;

public enum CourseLessonType {

	DOCUMENT, VIDEO, TEXT, TESTPAPER, AUDIO, PPT, EMPTY, CHAPTER, UNIT,LIVE;

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

    public StringBuilder getType()
    {
        StringBuilder stringBuilder = new StringBuilder(toString().toLowerCase());
        char first = Character.toUpperCase(stringBuilder.charAt(0));
        stringBuilder.deleteCharAt(0);
        stringBuilder.insert(0, first);
        return stringBuilder;
    }
}
