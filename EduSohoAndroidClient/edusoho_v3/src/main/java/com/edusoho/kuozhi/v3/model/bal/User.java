package com.edusoho.kuozhi.v3.model.bal;

import java.io.Serializable;

/**
 * Created by howzhi on 14-5-25.
 */
public class User implements Serializable {
    public String nickname;
    public String email;
    public String password;
    public int id;
    public String avatar;
    public UserRole[] roles;
    public String uri;
    public String title;
    public String type;
    public String point;
    public String mediumAvatar;
    public String createdTime;
    public String about;
    public String role;
    /**
     * 关注
     */
    public String following;
    /**
     * 粉丝
     */
    public String follower;

    public Vip vip;

    public String thirdParty;

    public String getMediumAvatar() {
        int schemIndex = mediumAvatar.lastIndexOf("http://");
        if (schemIndex != 0) {
            return mediumAvatar.substring(schemIndex);
        }
        return mediumAvatar;
    }
}
