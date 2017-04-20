package com.edusoho.kuozhi.v3.util.helper;

import android.content.Context;
import android.util.Log;

import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.bal.m3u8.M3U8DbModel;
import com.edusoho.kuozhi.v3.model.sys.Cache;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.sql.SqliteUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import cn.trinea.android.common.util.FileUtils;

/**
 * Created by suju on 17/4/8.
 */

public class LocalLessonHelper {

    private int mLessonId;
    private Context mContext;

    public LocalLessonHelper(Context context, int lessonId) {
        this.mLessonId = lessonId;
        this.mContext = context;
    }

    private void saveExtXKeyFile(File dir, String key) {
        SqliteUtil sqliteUtil = SqliteUtil.getUtil(mContext);
        Cache keyCache = sqliteUtil.query(
                "select * from data_cache where key=? and type=?",
                key,
                Const.CACHE_KEY_TYPE
        );
        if (keyCache != null) {
            FileUtils.writeFile(new File(dir, key).getAbsolutePath(), keyCache.value);
        }
    }

    private File getVideoDir() {
        File workSpace = EdusohoApp.getWorkSpace();
        if (workSpace == null) {
            return null;
        }
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();

        StringBuffer dirBuilder = new StringBuffer(workSpace.getAbsolutePath());
        dirBuilder.append("/videos/")
                .append(user.id)
                .append("/")
                .append(school.getDomain());

        return new File(dirBuilder.toString());
    }

    public File createLocalPlayListFile() {
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();
        M3U8DbModel m3U8DbModel = M3U8Util.queryM3U8Model(mContext, user.id, mLessonId, school.getDomain(), M3U8Util.ALL);
        if (m3U8DbModel != null) {
            File videoDir = getVideoDir();
            BufferedReader reader = null;
            File cacheStorage = mContext.getDir("cache", Context.MODE_PRIVATE);
            File tempFile = new File(cacheStorage, "temp.m3u8");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));
                reader = new BufferedReader(new StringReader(m3U8DbModel.playList));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    int keyIndex = line.indexOf("ext_x_key");
                    if (keyIndex != -1) {
                        keyIndex = keyIndex + "ext_x_key".length() + 1;
                        String extXKey = line.substring(keyIndex, keyIndex + 32);
                        writer.write(line.replaceAll(
                                "http://localhost:8800/ext_x_key/" + extXKey,
                                String.format("file://%s/ext_x_key/%s", cacheStorage.getAbsolutePath(), extXKey)));
                        writer.write("\r\n");
                        saveExtXKeyFile(cacheStorage, "ext_x_key/" + extXKey);
                        continue;
                    }
                    writer.write(line.replaceAll("http://localhost:8800", "file://" + videoDir.getAbsolutePath()));
                    writer.write("\r\n");
                }

                return tempFile;
            } catch (Exception e) {
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                } catch (Exception e) {
                }
            }
        }

        return null;
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
