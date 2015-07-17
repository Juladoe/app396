package com.edusoho.kuozhi.model;

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
