package com.edusoho.kuozhi.model;

import java.io.Serializable;

/**
 * Created by howzhi on 14-5-25.
 */
public class User implements Serializable {
    public String nickname;
    public String email;
    public int id;
    public String smallAvatar;
    public UserRole[] roles;
    public String salt;
    public String uri;
    public String title;
    public String tags;
    public String type;
    public String point;
    public String coin;
    public String mediumAvatar;
    public String largeAvatar;
    public String emailVerified;
    public String setup;
    public String promoted;
    public String promotedTime;
    public String locked;
    public String loginTime;
    public String loginIp;
    public String loginSessionId;
    public String approvalTime;
    public String approvalStatus;
    public String newMessageNum;
    public String newNotificationNum;
    public String createdIp;
    public String createdTime;
    public String about;

    public Vip vip;
}
