package com.edusoho.kuozhi.v3.model.bal.Discovery;

/**
 * Created by su on 2016/2/19.
 */
public class DiscoveryCardEntity {

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

    public DiscoveryCardEntity()
    {
        this.mIsEmpty = false;
    }

    public DiscoveryCardEntity(boolean isEmpty)
    {
        this.mIsEmpty = isEmpty;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }
}
