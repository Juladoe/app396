package com.edusoho.kuozhi.v3.model.result;

import com.edusoho.kuozhi.v3.entity.user.UserEntity;
import com.edusoho.kuozhi.v3.model.sys.School;

public class UserResult {
    public UserEntity userEntity;
    public String token;
    public School site;

    public UserResult() {

    }

    public UserResult(UserEntity userEntity, String token, School site) {
        this.userEntity = userEntity;
        this.token = token;
        this.site = site;
    }
}
