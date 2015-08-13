package com.edusoho.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.edusoho.kuozhi.v3.EdusohoApp;
import org.junit.Test;


/**
 * Created by howzhi on 15/8/13.
 */

@SmallTest
public class EduSohoApplicationTest extends ApplicationTestCase<EdusohoApp> {

    private EdusohoApp mEdusohoApp;
    public EduSohoApplicationTest() {
        super(EdusohoApp.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedPreferences sp = getContext().getSharedPreferences("config", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("showSplash", false);
        editor.putBoolean("registPublicDevice", false);
        editor.putBoolean("startWithSchool", false);
        editor.putBoolean("startWithSchool", false);
        editor.putInt("msgSound", 1);
        editor.putInt("msgVibrate", 1);
        editor.commit();

        createApplication();
        mEdusohoApp = getApplication();
    }

    @Test
    public void testAppliction() {
        assertNotNull(mEdusohoApp);
        assertNotNull(mEdusohoApp.mVolley);
        assertNotNull(mEdusohoApp.mEngine);
        assertNotNull(mEdusohoApp.gson);
        assertNotNull(mEdusohoApp.runTask);
    }

    @Test
    public void testAppHostAndDomain() {
        String host = getContext().getString(R.string.app_host);
        String domain = getContext().getString(R.string.app_domain);

        assertEquals(host, mEdusohoApp.host);
        assertEquals(domain, mEdusohoApp.domain);
    }

    @Test
    public void testAppConfit() {
        assertNotNull(mEdusohoApp.config);
        assertEquals(false, mEdusohoApp.config.showSplash);
    }
}
