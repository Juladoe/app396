package com.edusoho.kuozhi.v3.model.sys;

/**
 * Created by su on 2016/2/19.
 */
public class FindCardEntity {

    public String picture;

    public String title;

    public double price;

    public int studentNum;

    public String type;

    public String startTime;

    public String endTime;

    public String avatar;

    public String nickname;

    public boolean mIsEmpty;

    public FindCardEntity()
    {
        this.mIsEmpty = false;
    }

    public FindCardEntity(boolean isEmpty)
    {
        this.mIsEmpty = isEmpty;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }
}
