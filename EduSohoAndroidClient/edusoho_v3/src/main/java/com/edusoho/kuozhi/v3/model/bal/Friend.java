package com.edusoho.kuozhi.v3.model.bal;

/**
 * Created by Melomelon on 2015/6/2.
 */
public class Friend {
    public Friend(String name) {
        this.name = name;
    }

    public Friend(int avatarID, String name) {
        this.avatarID = avatarID;
        this.name = name;
    }

    public int avatarID;
    public String avatar;
    public String name;
}
