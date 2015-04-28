package com.edusoho.kuozhi.v3.util.server;


import java.io.IOException;

import com.belladati.httpclientandroidlib.ConnectionClosedException;
import com.belladati.httpclientandroidlib.HttpException;
import com.belladati.httpclientandroidlib.HttpServerConnection;
import com.belladati.httpclientandroidlib.protocol.BasicHttpContext;
import com.belladati.httpclientandroidlib.protocol.HttpContext;
import com.belladati.httpclientandroidlib.protocol.HttpService;

/**
 * Created by howzhi on 14-10-24.
 */
public class WorkThread extends Thread {

    private final HttpService httpservice;
    private final HttpServerConnection conn;

    public WorkThread(HttpService httpservice, HttpServerConnection conn) {
        super();
        this.httpservice = httpservice;
        this.conn = conn;
    }

    @Override
    public void run() {
        HttpContext context = new BasicHttpContext();
        try {
            while (!Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
            }
        } catch (ConnectionClosedException ex) {
            System.err.println("Client closed connection");
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
        } catch (HttpException ex) {
            System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
        } finally {
            try {
                this.conn.shutdown();
            } catch (IOException ignore) {
            }
        }
    }

}