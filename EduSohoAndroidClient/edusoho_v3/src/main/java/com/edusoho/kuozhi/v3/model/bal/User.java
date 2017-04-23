package com.edusoho.kuozhi.v3.model.bal;

import com.edusoho.kuozhi.clean.bean.innerbean.Avatar;

import java.io.Serializable;

/**
 * Created by howzhi on 14-5-25.
 */
public class User implements Serializable {
    public String nickname;
    public String email;
    public String password;
    public int id;
    public UserRole[] roles;
    public String uri;
    public String title;
    public String type;
    public String point;
    public String createdTime;
    public String about;
    public String role;
    public String mediumAvatar;
    public Avatar avatar;

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
        int schemIndex = (avatar == null ? mediumAvatar.lastIndexOf("http://") : avatar.middle.lastIndexOf("http://"));
        if (schemIndex != -1) {
            return avatar == null ? mediumAvatar.substring(schemIndex) : avatar.middle.substring(schemIndex);
        }
        if (avatar != null && avatar.middle.startsWith("//")) {
            return "http:" + avatar.middle;
        } else if(mediumAvatar.startsWith("//")){
            return "http:" + mediumAvatar;
        }
        return avatar == null ? mediumAvatar : avatar.middle;
    }

    public String userRole2String() {
        if (roles == null || roles.length == 0) {
            return "学员";
        }

        StringBuilder sb = new StringBuilder();
        for (UserRole userRole : roles) {
            if (userRole != null) {
                sb.append(userRole.getRoleName()).append(" ");
            }
        }

        return sb.toString();
    }
}
