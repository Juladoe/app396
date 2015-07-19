package com.edusoho.kuozhi.model;

public class TokenResult{
	public String token;
	public User user;
    public School site;

    @Override
    public String toString() {
        return "TokenResult{" +
                "token='" + token + '\'' +
                ", user=" + user +
                ", site=" + site +
                '}';
    }
}
