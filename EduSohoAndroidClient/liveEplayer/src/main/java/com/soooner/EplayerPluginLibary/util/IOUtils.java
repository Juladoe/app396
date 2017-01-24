package com.soooner.EplayerPluginLibary.util;


import android.util.Log;

import java.io.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: yangjunhai
 * Date: 11-6-21
 * Time: 下午2:58
 * copy from apache-io
 */
public class IOUtils {
    
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException ioe) {
        }
    }

    public static byte[] toByteArray(InputStream input)
            throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static String toString(InputStream input)
            throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static int copy(InputStream input, OutputStream output)
            throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void copy(InputStream input, Writer output)
            throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }


    public static int copy(Reader input, Writer output)
            throws IOException {
        long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(Reader input, Writer output)
            throws IOException {
        char[] buffer = new char[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 读取指定流中内容，分段读取，起始内容如果不匹配content直接退出
     * 不支持类似中文多字节内容
     * @param input InputStream
     * @param content 对应的匹配内容，分段读取内容宽度等于字段宽度两倍
     * @return string
     * @throws IOException
     */
    public static String readByLength(InputStream input, String content) throws IOException {

        if(input == null || StringUtils.isEmpty(content)){
            return null;
        }

        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[content.length() * 2];
        InputStreamReader reader = new InputStreamReader(input);
        int times = 0;
        int num;
        while(-1 != (num = reader.read(buffer))) {
            Log.d("LU Test", "------------:"+ Arrays.toString(buffer));
            builder.append(buffer, 0, num);
            //对比第一次截取内容是否匹配content,不匹配直接退出，不再读取新内容
            if(times == 0 && !new String(buffer).startsWith(content)){
                Log.d("LU Test", "------------:Not match !");
                return null;
            }
            times++;
        }
        //只获取第一行内容，清除地址末尾换行符后面的所有内容
        String result = builder.toString();
        if(StringUtils.isEmpty(result)){
            return null;
        }
        result = result.split("\\n")[0];
        if(StringUtils.isEmpty(result)){
            return null;
        }
        return result;
    }



}