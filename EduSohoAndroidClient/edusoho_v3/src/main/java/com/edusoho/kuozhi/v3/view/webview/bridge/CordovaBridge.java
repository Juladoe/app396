package com.edusoho.kuozhi.v3.view.webview.bridge;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.edusoho.kuozhi.v3.model.htmlapp.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by howzhi on 15/4/21.
 */
public class CordovaBridge extends CoreBridge {

    public CordovaBridge(Context context)
    {
        super(context);
    }

    @JavascriptInterface
    public void version(JSONArray args)
    {
        Log.d(TAG, args.toString());
    }

    @JavascriptInterface
    public Object create(JSONArray args)
    {
        try {
            String className = args.getString(0);
            Class newClass = Class.forName(className);
            return newClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @JavascriptInterface
    public void showImages(JSONArray args) throws JSONException
    {
        int index = args.getInt(0);
        JSONArray images = args.getJSONArray(1);
        String[] strings = JsonArrayToStringArray(images);
        //ImageViewActivity.show(mContext, index, strings);
    }

    @JavascriptInterface
    public void createMenu(JSONArray args)
    {
        //JSONObject menuJson = args.isNull(0) ? null : args.getJSONObject(0);
        /*
        Activity appActivity = (Activity) this.cordova.getActivity();
        Menu menu = parseMenu(appActivity, menuJson);
        appActivity.setMenu(menu);
        appActivity.supportInvalidateOptionsMenu();
        */
    }

    private Menu parseMenu(Activity appActivity, JSONObject menuJson)
    {
        if (menuJson == null) {
            return null;
        }
        Menu menu = new Menu();
        try {
            menu.name = menuJson.getString("name");
            int iconId = appActivity.getResources().getIdentifier(
                    menuJson.getString("icon"), "drawable", appActivity.getPackageName());
            menu.icon = iconId;
            menu.action = menuJson.getString("action");
            JSONArray array = menuJson.getJSONArray("item");
            if (array == null) {
                menu.item = new Menu[0];
            } else {
                Menu[] menuItems = new Menu[0];
                int length = array.length();
                for (int i=0; i < length; i++) {
                    menuItems[i] = parseMenu(appActivity, array.getJSONObject(i));
                }
                menu.item = menuItems;
            }
        } catch (Exception e) {
            Log.d(null, e.toString());
        }

        return menu;
    }

    private String[] JsonArrayToStringArray(JSONArray images)
    {
        int length = images.length();
        String[] strs = new String[length];
        try {
            for (int i=0; i < length; i++) {
                strs[i] = images.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return strs;
    }
}
