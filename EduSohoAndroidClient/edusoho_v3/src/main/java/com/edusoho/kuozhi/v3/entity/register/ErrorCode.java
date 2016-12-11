package com.edusoho.kuozhi.v3.entity.register;

/**
 * Created by DF on 2016/12/9.
 */

public class ErrorCode {
    public ErrorBean error;
    public static class ErrorBean {
        /**
         * code : 500
         * message : 图形验证码错误
         */
        public String code;
        public String message;

    }
}
