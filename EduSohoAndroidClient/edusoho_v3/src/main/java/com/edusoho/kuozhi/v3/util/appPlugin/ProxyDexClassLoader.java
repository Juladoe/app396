package com.edusoho.kuozhi.v3.util.appPlugin;

import android.util.Log;

import dalvik.system.DexClassLoader;

/**
 * Created by howzhi on 15/10/12.
 */
public class ProxyDexClassLoader extends DexClassLoader {

    public ProxyDexClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent)
    {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Log.d("ProxyDexClassLoader", name);

        return super.findClass(name);
    }
}
