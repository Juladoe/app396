package com.edusoho.kuozhi.v3.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by su on 2015/12/2.
 */
public class AssetsUtil {

    public static InputStream open(Context context, String name) throws IOException{
        InputStream inputStream = context.getAssets().open(name);
        M3U8Util.DigestInputStream digestInputStream = new M3U8Util.DigestInputStream(
                inputStream, context.getPackageName(), false);

        return digestInputStream;
    }
}
