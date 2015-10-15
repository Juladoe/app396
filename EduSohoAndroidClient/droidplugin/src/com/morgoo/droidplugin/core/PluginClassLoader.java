/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.core;

import android.util.Log;
import dalvik.system.DexClassLoader;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/4.
 */
public class PluginClassLoader extends DexClassLoader {

    public PluginClassLoader(String apkfile, String optimizedDirectory, String libraryPath, ClassLoader systemClassLoader) {
        super(apkfile, optimizedDirectory, libraryPath, systemClassLoader);
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Log.d("PluginClassLoader", className);
        return super.loadClass(className, resolve);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Log.d("PluginClassLoader", className);
        return super.loadClass(className);
    }
}
