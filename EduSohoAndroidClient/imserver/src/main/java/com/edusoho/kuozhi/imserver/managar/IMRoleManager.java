package com.edusoho.kuozhi.imserver.managar;

import android.content.Context;
import android.text.TextUtils;

import com.edusoho.kuozhi.imserver.entity.Role;
import com.edusoho.kuozhi.imserver.util.RoleDbHelper;
import java.util.Map;

/**
 * Created by 菊 on 2016/5/15.
 */
public class IMRoleManager {

    private Context mContext;

    public IMRoleManager(Context context)
    {
        this.mContext = context;
    }

    public Map<Integer, Role> getRoleMap(int[] rid) {
        return new RoleDbHelper(mContext).getRoleMap(rid);
    }

    public Role getRole(String type, int rid) {
        if (TextUtils.isEmpty(type)) {
            return new Role();
        }
        return new RoleDbHelper(mContext).getRoleByType(type, rid);
    }

    public long createRole(Role role) {
        return new RoleDbHelper(mContext).save(role);
    }

    public int updateRole(Role role) {
        return new RoleDbHelper(mContext).update(role);
    }
}
