package com.edusoho.kuozhi.v3.model;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.push.New;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class InitModelTool {

    public static List<New> initNewsItemList() {

        List<New> list = new ArrayList<>();
        New newModel = new New();
        newModel.setFromId(32);
        newModel.setTitle("suju");
        newModel.setContent("请问下一章什么时候更新？");
        newModel.setCreatedTime(1413796863);
        newModel.setImgUrl("http://trymob.edusoho.cn/assets/img/default/avatar.png?6.1.0");
        newModel.setUnread(1);
        newModel.setType("teacher");
        if (EdusohoApp.app.loginUser != null) {
            newModel.setBelongId(EdusohoApp.app.loginUser.id);
        }
        list.add(newModel);
        return list;
    }

    public static List<User> getUserList() {
        List<User> list = new ArrayList<>();
        User u1 = new User();
        u1.id = 34;
        u1.nickname = "suju";
        list.add(u1);
        return list;
    }
}
