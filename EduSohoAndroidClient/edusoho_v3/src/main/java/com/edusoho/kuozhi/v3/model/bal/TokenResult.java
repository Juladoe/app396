package com.edusoho.kuozhi.v3.model.bal;

import com.edusoho.kuozhi.v3.model.sys.School;

public class TokenResult {
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
