package com.edusoho.kuozhi.v3.model.bal.article;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.model.bal.push.Chat;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
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
        articleChat.createdTime = (int) (System.currentTimeMillis() / 1000);
        articleChat.articleList = articleList;
        return articleChat;
    }

    private ArticleChat(){
    }

    public ArticleChat(Chat chat)
    {
        Article article = new Article();

        V2CustomContent.BodyEntity bodyEntity = new Gson().fromJson(chat.getContent(), V2CustomContent.BodyEntity.class);
        article.body = bodyEntity.getContent();
        article.title = bodyEntity.getTitle();
        article.id = bodyEntity.getId();

        this.nickName = EdusohoApp.app.domain;
        this.articleList = new ArrayList<>();
        this.articleList.add(article);
    }

    public ArticleChat(WrapperXGPushTextMessage message)
    {
        super(message);
        Article article = new Article();

        Gson gson = new Gson();
        V2CustomContent.BodyEntity bodyEntity = gson.fromJson(
                message.getContent(), V2CustomContent.BodyEntity.class);
        article.body = bodyEntity.getContent();
        article.title = bodyEntity.getTitle();
        article.id = bodyEntity.getId();
        article.picture = bodyEntity.getImage();

        this.nickName = EdusohoApp.app.domain;
        this.articleList = new ArrayList<>();
        this.articleList.add(article);
    }
}
