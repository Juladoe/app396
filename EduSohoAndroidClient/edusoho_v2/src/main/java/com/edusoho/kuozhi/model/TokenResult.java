package com.edusoho.kuozhi.model;

import com.edusoho.kuozhi.model.School;
import com.edusoho.kuozhi.model.User;

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
