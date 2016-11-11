package com.edusoho.kuozhi.v3.service.message.push;

import android.content.ContentValues;
import android.content.Context;
import com.edusoho.kuozhi.imserver.helper.IDbManager;
import com.edusoho.kuozhi.imserver.util.DbHelper;
import com.edusoho.kuozhi.v3.model.bal.push.Notify;
import com.edusoho.kuozhi.v3.util.AppUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 菊 on 2016/4/29.
 */
public class NotifyDbHelper {

    private static final String TABLE = "school_nofity";
    private DbHelper mDbHelper;

    public NotifyDbHelper(Context context, IDbManager dbManager) {
        mDbHelper = new DbHelper(context, dbManager);
    }

    public void createNotify(Notify notify) {
        ContentValues cv = new ContentValues();
        cv.put("content", notify.getContent());
        cv.put("title", notify.getTitle());
        cv.put("type", notify.getType());
        cv.put("createdTime", notify.getCreatedTime());
        mDbHelper.insert(TABLE, cv);
    }

    private Notify createNofity(HashMap<String, String> dataMap) {
        Notify notify = new Notify();
        notify.setContent(dataMap.get("content"));
        notify.setTitle(dataMap.get("title"));
        notify.setType(dataMap.get("type"));
        notify.setCreatedTime(AppUtil.parseLong(dataMap.get("createdTime")));

        return notify;
    }

    public List<Notify> getNofityList(int start, int limit) {
        ArrayList<HashMap<String, String>> arrayList = mDbHelper.queryBySortAndLimit(
                TABLE, null, null, "createdTime desc", String.format("%d, %d", start, limit));

        List<Notify> notifyList = new ArrayList<>();
        if (arrayList == null || arrayList.isEmpty()) {
            return notifyList;
        }

        for (HashMap<String, String> arrayMap : arrayList) {
            notifyList.add(createNofity(arrayMap));
        }

        return notifyList;
    }

}
