package com.edusoho.kuozhi.v3.plugin;

import android.util.Log;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.ui.fragment.FragmentNavigationDrawer;
import com.edusoho.kuozhi.v3.util.Const;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by JesseHuang on 15/6/2.
 */
public class MenuClickPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("MenuClickPlugin-->", "MenuClickPlugin");
        if (action.equals("open")) {
            String message = args.getString(0);
            if (message.equals("open")) {
                EdusohoApp.app.sendMsgToTarget(Const.MAIN_MENU_OPEN, null, FragmentNavigationDrawer.class);
            }
        }
        return super.execute(action, args, callbackContext);
    }

}
