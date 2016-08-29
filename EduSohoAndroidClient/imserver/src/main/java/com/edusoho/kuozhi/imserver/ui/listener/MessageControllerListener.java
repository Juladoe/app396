package com.edusoho.kuozhi.imserver.ui.listener;

import com.edusoho.kuozhi.imserver.entity.Role;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by suju on 16/8/27.
 */
public interface MessageControllerListener {

    void createConvNo(ConvNoCreateCallback callback);

    void createRole(RoleUpdateCallback callback);

    void onShowImage(int index, ArrayList<String> imageList);

    void onShowUser(Role role);

    void onShowWebPage(String url);

    Map<String, String> getRequestHeaders();

    /*
        callback
     */
    interface RoleUpdateCallback
    {
        void onCreateRole(Role role);
    }

    interface ConvNoCreateCallback
    {
        void onCreateConvNo(String convNo);
    }
}
