package com.edusoho.kuozhi.v3.model.bal.push;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by howzhi on 15/9/29.
 */
public class RedirectBody implements Serializable {

    public String type;
    public String fromType;
    public int id;
    public String title;
    public String image;
    public String content;
    public String url;
    public String source;

    public static RedirectBody createByJsonObj(JSONObject body) {

        RedirectBody redirectBody = new RedirectBody();
        try {
            redirectBody.type = body.getString("type");
            redirectBody.fromType = body.getString("fromType");
            redirectBody.title = body.getString("title");
            redirectBody.image = body.getString("image");
            redirectBody.content = body.getString("content");
            redirectBody.url = body.getString("url");
            redirectBody.source = body.getString("source");
            redirectBody.id = body.getInt("id");
        } catch (JSONException e) {

        }

        return redirectBody;
    }
}
