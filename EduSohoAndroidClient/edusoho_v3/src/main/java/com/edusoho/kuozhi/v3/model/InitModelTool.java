package com.edusoho.kuozhi.v3.model;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.ChatMessage;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.news.NewsEnum;
import com.edusoho.kuozhi.v3.model.bal.news.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class InitModelTool {

    public static List<NewsItem> initNewsItemList1() {
        List<NewsItem> list = new ArrayList<>();
        NewsItem item1 = new NewsItem();
        item1.srcUrl = "http://demo.edusoho.com/files/user/2014/09-04/154726ea4a64834193.jpg?5.5.11";
        item1.title = "Jesse";
        item1.content = "每星期五晚上更新。";
        item1.unread = 0;
        item1.type = NewsEnum.FRIEND;
        item1.postTime = "9:08";
        list.add(item1);
        return list;
    }

    public static List<NewsItem> initNewsItemList() {
        List<NewsItem> list = new ArrayList<>();
        NewsItem item1 = new NewsItem();
        item1.srcUrl = "http://demo.edusoho.com/files/course/2015/03-17/154654ec9416045105.jpg?5.5.10";
        item1.title = "课程功能示范";
        item1.content = "从2014年10月18日00:00起，EduSoho的安装教程、使用教程将迁移至...";
        item1.unread = 2;
        item1.type = NewsEnum.COURSE;
        item1.postTime = "9:08";
        list.add(item1);

        NewsItem item2 = new NewsItem();
        item2.srcUrl = "http://demo.edusoho.com/files/course/2015/03-17/170727fdd599524908.jpg?5.5.10";
        item2.title = "会员课程体验";
        item2.content = "会员课程体验，EduSoho课程可以设置售价和会员课程，如果将课程设置为某一等级会员免费学，那么该等级会员以及更高等级的会员都可免费学习。";
        item2.unread = 4;
        item2.type = NewsEnum.COURSE;
        item2.postTime = "18:20";
        list.add(item2);

        NewsItem item3 = new NewsItem();
        item3.srcUrl = "http://demo.edusoho.com/files/default/2015/05-13/152712005528946762.jpg?5.5.10";
        item3.title = "学习卡体验课程";
        item3.content = "体验购买课程功能，每月推送新功能预告！...";
        item3.unread = 0;
        item3.type = NewsEnum.COURSE;
        item3.postTime = "09:20";
        list.add(item3);

        NewsItem item4 = new NewsItem();
        item4.srcUrl = "http://demo.edusoho.com/files/default/2015/05-13/152651b0432b052124.jpg?5.5.10";
        item4.title = "suju";
        item4.content = "今天《Android M 初步了解》更新了，你看了吗？";
        item4.unread = 1;
        item4.type = NewsEnum.FRIEND;
        item4.postTime = "18:11";
        list.add(item4);

//        NewsItem item5 = new NewsItem();
//        item5.srcUrl = "http://demo.edusoho.com/files/default/2015/05-21/234332457375590099.png?5.5.10";
//        item5.title = "李老师";
//        item5.content = "亲爱的小伙伴们，为了加深大家对正则表达式的理解，为大家精心准备了一门视频课程《鬼斧神工之正则表达式》";
//        item5.unread = 2;
//        item5.type = NewsEnum.TEACHER;
//        item5.postTime = "21:11";
//        list.add(item5);

        return list;
    }

    public static List<ChatMessage> initMessageList() {
        User loginUser = EdusohoApp.app.loginUser;
        User oppositeUser = getUserList().get(0);
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage m1 = new ChatMessage();
        m1.id = 1;
        m1.fromId = 68;
        m1.toId = 34;
        m1.content = "老师，你好";
        m1.createdTime = "2015-06-03T10:01:14+08:00";
        m1.createdUser = loginUser;
        list.add(m1);

        ChatMessage m2 = new ChatMessage();
        m2.id = 2;
        m2.fromId = 34;
        m2.toId = 68;
        m2.content = "你好";
        m2.createdTime = "2015-06-03T10:02:11+08:00";
        m2.createdUser = oppositeUser;
        list.add(m2);

        ChatMessage m3 = new ChatMessage();
        m3.id = 3;
        m3.fromId = 68;
        m3.toId = 34;
        m3.content = "老师，请问下一节内容什么时候更新？";
        m3.createdTime = "2015-06-03T10:02:32+08:00";
        m3.createdUser = loginUser;
        list.add(m3);

        ChatMessage m4 = new ChatMessage();
        m4.id = 4;
        m4.fromId = 34;
        m4.toId = 68;
        m4.content = "每星期五晚上更新。";
        m4.createdTime = "2015-06-03T10:02:35+08:00";
        m4.createdUser = oppositeUser;
        list.add(m4);

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
