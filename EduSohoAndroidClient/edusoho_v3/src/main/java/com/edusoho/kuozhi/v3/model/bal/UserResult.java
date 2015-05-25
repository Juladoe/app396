package com.edusoho.kuozhi.v3.model.bal;

import com.edusoho.kuozhi.v3.model.sys.MetaResult;
import com.edusoho.kuozhi.v3.model.sys.School;

public class UserResult extends MetaResult {
    public User data;
    public School site;

    @Override
    public String toString() {
        return "TokenResult{" +
                "token='" + data.token + '\'' +
                ", data=" + data +
                ", site=" + site +
                '}';
    }
}
