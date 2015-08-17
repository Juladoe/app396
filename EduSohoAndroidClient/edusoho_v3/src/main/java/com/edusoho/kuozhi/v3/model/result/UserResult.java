package com.edusoho.kuozhi.v3.model.result;

import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.School;

public class UserResult {
    public User user;
    public String token;
    public School site;

    public UserResult() {

    }

    public UserResult(User user, String token, School site) {
        this.user = user;
        this.token = token;
        this.site = site;
    }
}
