package com.edusoho.kuozhi.ui.htmlView.plugin;

import android.os.Bundle;
import android.util.Log;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.model.HtmlApp.Menu;
import com.edusoho.kuozhi.ui.htmlView.EduHtmlAppActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by howzhi on 14/11/28.
 */
public class CordovaUtil extends CordovaPlugin {

    private enum ACTION {
        showImages, createMenu, EMPTY;

        public static ACTION parse(String name)
        {
            try {
                return ACTION.valueOf(name);
            } catch (Exception e) {
                return EMPTY;
            }
        }
    }

    public CordovaUtil(){
    }

    @Override
    public boolean execute(
            String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        ACTION method = ACTION.parse(action);
        switch (method) {
            case showImages:
                int index = args.getInt(0);
                JSONArray images = args.getJSONArray(1);
                showImages(index, images);
                callbackContext.success();
                break;
            case createMenu:
                createMenu(args.isNull(0) ? null : args.getJSONObject(0));
                break;
            default:
        }

        return super.execute(action, args, callbackContext);
    }

    private void createMenu(JSONObject menuJson)
    {
        EduHtmlAppActivity appActivity = (EduHtmlAppActivity) this.cordova.getActivity();
        Menu menu = parseMenu(appActivity, menuJson);
        appActivity.setMenu(menu);
        appActivity.supportInvalidateOptionsMenu();
    }

    private Menu parseMenu(EduHtmlAppActivity appActivity, JSONObject menuJson)
    {
        Log.d(null, "menuJson->" + menuJson);
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

    private void showImages(int index, JSONArray images)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        String[] strings = JsonArrayToStringArray(images);
        bundle.putStringArray("images", strings);
        EdusohoApp.app.mEngine.runNormalPluginWithBundle(
                "ViewPagerActivity", EdusohoApp.app, bundle);
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
            //
        }
        return strs;
    }
}
