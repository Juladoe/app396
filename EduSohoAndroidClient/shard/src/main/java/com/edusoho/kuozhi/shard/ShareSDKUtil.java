package com.edusoho.kuozhi.shard;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by su on 2015/12/2.
 */
public class ShareSDKUtil {

    public void initSDK(Context context) {
        ShareSDK.initSDK(context, "41f51eeb5d88");
        initDevInfo(context);
    }

    private void initDevInfo(Context context) {

        try {
            XmlPullParser parser = context.getResources().getXml(R.xml.sharesdk);
            for(int type = parser.getEventType(); type != 1; type = parser.next()) {
                if (type == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    int attrCount = parser.getAttributeCount();
                    HashMap<String,Object> hashMap = new HashMap<String, Object>();
                    for (int attriIndex = 0; attriIndex < attrCount; ++ attriIndex) {
                        String attributeName = parser.getAttributeName(attriIndex);
                        String attributeValue = parser.getAttributeValue(attriIndex).trim();
                        hashMap.put(attributeName, attributeValue);
                    }
                    ShareSDK.setPlatformDevInfo(name,hashMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
