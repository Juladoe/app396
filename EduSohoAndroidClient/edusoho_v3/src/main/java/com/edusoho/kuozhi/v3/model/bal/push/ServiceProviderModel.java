package com.edusoho.kuozhi.v3.model.bal.push;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.google.gson.Gson;

/**
 * Created by howzhi on 15/9/24.
 */
public class ServiceProviderModel {

    public int id;
    public int spId;
    public int toId;
    public String title;
    public String content;
    public String type;
    public String body;
    public int createdTime;

    public ServiceProviderModel(){
    }
}
