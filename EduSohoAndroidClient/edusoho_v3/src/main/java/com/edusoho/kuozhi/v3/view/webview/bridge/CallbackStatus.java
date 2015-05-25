package com.edusoho.kuozhi.v3.view.webview.bridge;

/**
 * Created by howzhi on 15/4/17.
 */
public class CallbackStatus {

    public static final int SUCCESS = 0;
    public static final int ERROR = -1;

    protected int status;
    protected Object message;

    public void setSuccess(Object message)
    {
        this.status = SUCCESS;
        this.message = message;
    }

    public void setError(Object message)
    {
        this.status = ERROR;
        this.message = message;
    }

    public int getStatus()
    {
        return status;
    }

    public Object getMessage()
    {
        return message;
    }
}
