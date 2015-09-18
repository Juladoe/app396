package com.edusoho.kuozhi.v3.model.bal.article;

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

        articleChat.id = (int) System.currentTimeMillis();
        articleChat.createdTime = (int) (System.currentTimeMillis() / 1000);
        articleChat.articleList = articleList;
        return articleChat;
    }

    public ArticleChat(){
    }

    public ArticleChat(WrapperXGPushTextMessage message)
    {
        super(message);
        V2CustomContent customContent = message.getV2CustomContent();
        Article article = new Article();

        this.articleList = new ArrayList<>();
        this.articleList.add(article);
    }
}
