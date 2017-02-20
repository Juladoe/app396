package com.test.talkfunplayer;


import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpRequest {

    public static int TIMOUT_MINILLIS = 5000;

    public static  void doGetAsyn(final String requestUrl,final IHttpRequestCallBack callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = doGet(requestUrl);
                if(callback != null){
                    callback.onRequestCompleted(result);
                }
            }
        }).start();
    }

    public static String doGet(final String requestUrl){
        URL url = null;
        HttpURLConnection urlConn = null;
        InputStream is = null;
        ByteArrayOutputStream bops = null;
        String result = null;
        try {
            url = new URL(requestUrl);
            urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setConnectTimeout(TIMOUT_MINILLIS);
            urlConn.setReadTimeout(TIMOUT_MINILLIS);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("accept","*/*");
           // urlConn.setRequestProperty("connection","K");
            int responseCode = urlConn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                is = urlConn.getInputStream();
                bops = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int len = -1;
                while ((len = is.read(data)) != -1){
                    bops.write(data,0,len);
                }
                bops.flush();
                result = bops.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bops != null){
                try {
                    bops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static  void doPostAsyn(final String requestUrl,final Map<String, String> params,final IHttpRequestCallBack callback){
        doPostAsyn(requestUrl,getRequestData(params),callback);
    }
    public static  void doPostAsyn(final String requestUrl,final String params,final IHttpRequestCallBack callback){
        new Thread()
        {
            public void run()
            {
                try
                {
                    String result = doPost(requestUrl, params);
                    if (callback != null)
                    {
                        callback.onRequestCompleted(result);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            };
        }.start();
    }

    public static  String doPost(final String requestUrl,String params){
        String result = null;
        URL url = null;
        HttpURLConnection urlConn = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            url = new URL(requestUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setReadTimeout(TIMOUT_MINILLIS);
            urlConn.setConnectTimeout(TIMOUT_MINILLIS);
           // urlConn.setRequestProperty("accept", "*/*");
           // urlConn.setRequestProperty("Content-Type",
            //        "application/x-www-form-urlencoded");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            if(!TextUtils.isEmpty(params)){
                // 获取URLConnection对象对应的输出流
                out = new PrintWriter(urlConn.getOutputStream());
                // 发送请求参数
                out.print(params);
                // flush输出流的缓冲
                out.flush();
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(urlConn.getInputStream()));
            String line;
            result = "";
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }


        return result;
    }

  /*  //方法：发送网络请求
    public void sendRequestWithGet(final String requestUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(requestUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    int status = urlConnection.getResponseCode();
                    if (status == 200) {
                        InputStream inputStream = urlConnection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String readLine;
                        while ((readLine = br.readLine()) != null) {
                            sb.append(readLine);
                        }
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = sb.toString();
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = REQUEST_ERROR;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Message message = new Message();
                    message.what = REQUEST_ERROR;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }

            }
        }).start();
    }*/

/*    public void sendRequestWithPost(final String requestUrl,Map<String, String> params){
        byte[] data = getRequestData(params).getBytes();
        HttpURLConnection urlConnection = null;
        try {
            URL url = null;
            try {

            url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }catch (MalformedURLException e) {
                e.printStackTrace();
            }

        urlConnection.setUseCaches(false);
        OutputStream outputStream = null;
        try {

            outputStream = urlConnection.getOutputStream();
            outputStream.write(data);
            int response = urlConnection.getResponseCode();
            String result = "";
            if(response == HttpURLConnection.HTTP_OK){
                InputStream inputStream = urlConnection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] readData = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(readData)) != -1){
                    byteArrayOutputStream.write(readData,0,len);
                }
                result = new String(byteArrayOutputStream.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

            return result;
    }*/


    private static String getRequestData(Map<String,String> params){
        StringBuffer strBuffer = new StringBuffer();

            try {
                for(Map.Entry<String,String> entry : params.entrySet()){
                    strBuffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), "utf-8"))
                            .append("&");
                }
                strBuffer.deleteCharAt(strBuffer.length() - 1);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        return strBuffer.toString();
    }

    public interface IHttpRequestCallBack {
        void onRequestCompleted(String responseStr);
       // void onIOError(String errorStr);
    }
}
