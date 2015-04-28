package com.edusoho.kuozhi.v3.model.bal;

import java.io.Serializable;

/**
 * Created by howzhi on 14-5-25.
 */
public class User implements Serializable {
    public String nickname;
    public String email;
    public String password;
    public String payPassword;
    public String payPasswordSalt;
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
    public String lastPasswordFailTime;
    public String lockDeadline;
    public String consecutivePasswordErrorTimes;
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
    public String signature;
    /**
     * 关注
     */
    public String following;
    /**
     * 粉丝
     */
    public String follower;

    public int dataType;


    public Vip vip;
}
