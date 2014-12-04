package com.edusoho.kuozhi.util.server;


import android.util.Log;

import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.server.handler.FileHandler;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ch.boye.httpclientandroidlib.impl.DefaultConnectionReuseStrategy;
import ch.boye.httpclientandroidlib.impl.DefaultHttpResponseFactory;
import ch.boye.httpclientandroidlib.impl.DefaultHttpServerConnection;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.CoreConnectionPNames;
import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.BasicHttpProcessor;
import ch.boye.httpclientandroidlib.protocol.HttpRequestHandler;
import ch.boye.httpclientandroidlib.protocol.HttpRequestHandlerRegistry;
import ch.boye.httpclientandroidlib.protocol.HttpService;
import ch.boye.httpclientandroidlib.protocol.ResponseConnControl;
import ch.boye.httpclientandroidlib.protocol.ResponseContent;
import ch.boye.httpclientandroidlib.protocol.ResponseDate;
import ch.boye.httpclientandroidlib.protocol.ResponseServer;

public class CacheServer extends Thread{

    private int port = Const.CACHE_PROT;
    private boolean isLoop;
    public static final String TAG = "CacheServer";
    private ActionBarBaseActivity mActivity;
    private ServerSocket mServerSocket;
    private HttpRequestHandlerRegistry mHttpRequestHandlerRegistry;

    public CacheServer(ActionBarBaseActivity activity)
    {
        this.mActivity = activity;
        // 创建HTTP请求执行器注册表
        mHttpRequestHandlerRegistry = new HttpRequestHandlerRegistry();
    }

    public CacheServer(ActionBarBaseActivity activity, int port)
    {
        this(activity);
        this.port = port;
    }

    public void addHandler(String filter, HttpRequestHandler handler)
    {
        mHttpRequestHandlerRegistry.register(filter, handler);
    }

    @Override
    public synchronized void start() {
        if (isLoop) {
            return;
        }
        super.start();
    }

    @Override
    public void run() {
        if (isLoop) {
            return;
        }
        init();
    }

    public void init()
    {
        mServerSocket = null;
        try {
            // 创建服务器套接字
            mServerSocket = new ServerSocket(port);
            // 创建HTTP协议处理器
            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            // 增加HTTP协议拦截器
            httpproc.addInterceptor(new ResponseDate());
            httpproc.addInterceptor(new ResponseServer());
            httpproc.addInterceptor(new ResponseContent());
            httpproc.addInterceptor(new ResponseConnControl());
            // 创建HTTP服务
            HttpService httpService = new HttpService(httpproc,
                    new DefaultConnectionReuseStrategy(),
                    new DefaultHttpResponseFactory());
            // 创建HTTP参数
            HttpParams params = new BasicHttpParams();
            params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
                            8 * 1024)
                    .setBooleanParameter(
                            CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER,
                            "Android Server/1.1");
            // 设置HTTP参数
            httpService.setParams(params);

            if (mHttpRequestHandlerRegistry.getHandlers().isEmpty()) {
                mHttpRequestHandlerRegistry.register("*", new FileHandler(mActivity.app.host, mActivity));
            }
            // 设置HTTP请求执行器
            httpService.setHandlerResolver(mHttpRequestHandlerRegistry);
			/* 循环接收各客户端 */
            isLoop = true;
            while (isLoop && !Thread.interrupted()) {
                // 接收客户端套接字
                Log.d(TAG, "serverSocket.accept");
                Socket socket = mServerSocket.accept();
                // 绑定至服务器端HTTP连接
                DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                conn.bind(socket, params);
                // 派送至WorkerThread处理请求
                Thread t = new WorkThread(httpService, conn);
                t.setDaemon(true); // 设为守护线程
                t.start();
                Log.d(TAG, "WorkThread Start");
            }
        } catch (IOException e) {
            isLoop = false;
            e.printStackTrace();
        } finally {
            try {
                if (mServerSocket != null) {
                    mServerSocket.close();
                    Log.d(null, "mServerSocket close");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close()
    {
        Log.d(TAG, "Cache exit");
        isLoop = false;
    }
}