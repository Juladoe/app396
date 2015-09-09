package com.edusoho.kuozhi.v3.model.provider;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by howzhi on 15/9/9.
 */
public class ProviderFactory {

    private ProviderFactory() {}
    private HashMap<String, ModelProvider> mProviderHashMap;
    private static ProviderFactory mFactory;

    public static synchronized ProviderFactory getFactory() {
        if (mFactory == null) {
            mFactory = new ProviderFactory();
        }

        return mFactory;
    }

    public boolean inject(String name, ModelProvider provider) {
        return mProviderHashMap.put(name, provider) != null;
    }

    public ModelProvider getProvider(String name) {
        return mProviderHashMap.get(name);
    }

    public ModelProvider create(Class targetClass, Context context) {
        ModelProvider provider = getProvider(targetClass.getSimpleName());
        if (provider == null) {
            try {
                provider = (ModelProvider) targetClass.getConstructor(Context.class).newInstance(context);
            } catch (Exception e) {
                //nothing
            }

            inject(targetClass.getSimpleName(), provider);
        }

        return provider;
    }
}
