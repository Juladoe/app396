package com.edusoho.kuozhi.v3.model.bal.article;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 15/9/17.
 */
public class ArticleChat extends Chat {

    public List<Article> articleList;

    public static ArticleChat create(List<Article> articleList) {

        ArticleChat articleChat = new ArticleChat();

        if (articleList == null) {
            articleList = new ArrayList<>();
        }

        articleChat.nickName = EdusohoApp.app.domain;
        articleChat.id = -1;

        Gson gson = new Gson();
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setId(-1);
        bodyEntity.setContent(gson.toJson(articleList));
        bodyEntity.setType("news.create");
        articleChat.setContent(gson.toJson(bodyEntity));
        articleChat.createdTime = (int) (System.currentTimeMillis() / 1000);
        articleChat.articleList = articleList;
        return articleChat;
    }

    private ArticleChat(){
    }

    public ArticleChat(Chat chat)
    {
        ArrayList<Article> arrayList = parseChatBody(chat.getContent());
        this.nickName = EdusohoApp.app.domain;
        this.articleList = arrayList;
    }

    private ArrayList<Article> parseChatBody(String body) {
        Gson gson = new Gson();
        V2CustomContent.BodyEntity bodyEntity = gson.fromJson(body, V2CustomContent.BodyEntity.class);
        ArrayList<Article> arrayList;
        try {
            arrayList = gson.fromJson(
                    bodyEntity.getContent(), new TypeToken<ArrayList<Article>>(){}.getType());
        } catch (Exception e) {
            arrayList = new ArrayList<>();
            Article article = new Article();
            article.body = bodyEntity.getContent();
            article.title = bodyEntity.getTitle();
            article.picture = bodyEntity.getImage();
            article.id = bodyEntity.getId();
            arrayList.add(article);
        }

        return arrayList;
    }

    public ArticleChat(WrapperXGPushTextMessage message)
    {
        super(message);
        ArrayList<Article> arrayList = parseChatBody(message.getContent());
        this.nickName = EdusohoApp.app.domain;
        this.articleList = arrayList;
    }
}
