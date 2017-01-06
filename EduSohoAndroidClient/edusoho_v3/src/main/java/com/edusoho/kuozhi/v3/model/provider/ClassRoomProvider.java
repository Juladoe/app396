package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.google.gson.reflect.TypeToken;

/**
 * Created by 菊 on 2016/5/19.
 */
public class ClassRoomProvider extends ModelProvider  {

    public ClassRoomProvider(Context context) {
        super(context);
    }

    public ProviderListener<Classroom> getClassRoom(int classRoomId) {
        RequestUrl requestUrl = new RequestUrl(String.format("%s/api/classrooms/%d", getHost(), classRoomId));
        requestUrl.getHeads().put("Auth-Token", getToken());
        requestUrl.setParams(new String[] {
                "classRoomId", String.valueOf(classRoomId)
        });
        RequestOption requestOption = buildSimpleGetRequest(
                requestUrl, new TypeToken<Classroom>(){});

        return requestOption.build();
    }
}
