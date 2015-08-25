package com.edusoho.kuozhi.v3.listener;

import com.edusoho.kuozhi.v3.util.Promise;

/**
 * Created by howzhi on 15/8/25.
 */
public interface PromiseCallback {

    public Promise invoke(Object obj);
}
