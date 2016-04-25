package com.edusoho.kuozhi.v3.factory.provider;

import android.content.Context;
import android.content.SharedPreferences;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.UtilFactory;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.util.AppUtil;

/**
 * Created by su on 2016/2/25.
 */
public class AppSettingProvider extends AbstractProvider {

    private static final String USER_SP = "token";

    private User mCurrentUser;

    public AppSettingProvider(Context context)
    {
        super(context);
        init();
    }

    private void init() {
        SharedPreferences sp = getSharedPreferences();
        mCurrentUser = getUtilFactory().getJsonParser().fromJson(AppUtil.encode2(sp.getString("userInfo", "")), User.class);
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setUser(User user) {
        this.mCurrentUser = user;
        saveUser(user);
    }

    private void saveUser(User user) {
        SharedPreferences sp = getSharedPreferences();
        sp.edit().putString("userInfo", AppUtil.encode2(getUtilFactory().getJsonParser().jsonToString(user))).commit();
    }

    protected SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(USER_SP, Context.MODE_PRIVATE);
    }

    protected UtilFactory getUtilFactory() {
        return FactoryManager.getInstance().create(UtilFactory.class);
    }
}
