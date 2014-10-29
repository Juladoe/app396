package com.edusoho.kuozhi.entity;

public class CourseItem {

	public String title;
	public String studentNum;
	public String teacher;
	public double rating;
	public String smallPicture;
	public double price;
	public String id;
	public String subtitle;
	public String expiryDay;
	public String showStudentNumType;
	public String income;
	public String lessonNum;
	public String ratingNum;
	public String categoryId;
	public String[] tags;
	public String middlePicture;
	public String largePicture;
	public String about;
	public String[] teacherIds;
	public String[] goals;
	public String[] audiences;
	public String recommended;
	public String recommendedSeq;
	public String recommendedTime;
	public String locationId;
	public String address;
	public String hitNum;
	public String userId;
	public String createdTime;
	public boolean mIsEmpty = false;
	
	public CourseItem() {
		super();
	}
	
	public CourseItem(boolean isEmpty) {
		super();
		mIsEmpty = isEmpty;
	}
	
}
