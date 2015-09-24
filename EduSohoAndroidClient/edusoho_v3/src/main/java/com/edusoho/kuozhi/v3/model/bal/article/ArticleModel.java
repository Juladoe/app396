package com.edusoho.kuozhi.v3.model.bal.article;

import com.edusoho.kuozhi.v3.model.bal.push.ServiceProviderModel;
import com.edusoho.kuozhi.v3.model.bal.push.V2CustomContent;
import com.edusoho.kuozhi.v3.model.bal.push.WrapperXGPushTextMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 15/9/17.
 */
public class ArticleModel extends ServiceProviderModel {

    public List<Article> articleList;

    public static ArticleModel create(int toId, List<Article> articleList) {

        ArticleModel articleModel = new ArticleModel();

        if (articleList == null) {
            articleList = new ArrayList<>();
        }

        articleModel.id = -1;
        articleModel.spId = 2;
        articleModel.type = "news";
        articleModel.toId = toId;
        Gson gson = new Gson();
        V2CustomContent.BodyEntity bodyEntity = new V2CustomContent.BodyEntity();
        bodyEntity.setId(-1);
        bodyEntity.setContent(gson.toJson(articleList));
        bodyEntity.setType("news.create");
        articleModel.body = gson.toJson(bodyEntity);
        articleModel.createdTime = (int) (System.currentTimeMillis() / 1000);
        articleModel.articleList = articleList;
        return articleModel;
    }

    private ArticleModel(){
    }

    public ArticleModel(ServiceProviderModel spModel)
    {
        this.id = spModel.id;
        this.spId = spModel.spId;
        this.title = spModel.title;
        this.content = spModel.content;
        this.toId = spModel.toId;
        this.body = spModel.body;
        this.createdTime = spModel.createdTime;
        ArrayList<Article> arrayList = parseChatBody(spModel.body);
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

    public ArticleModel(WrapperXGPushTextMessage message)
    {
        super(message);
        ArrayList<Article> arrayList = parseChatBody(this.body);
        this.articleList = arrayList;
    }
}
