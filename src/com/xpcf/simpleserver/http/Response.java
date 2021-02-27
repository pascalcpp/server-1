package com.xpcf.simpleserver.http;

import com.xpcf.simpleserver.util.HttpConstant;

/**
 * @author XPCF
 * @version 1.0
 * @date 2/27/2021 7:30 PM
 */
public class Response {

    private String header;

    private byte[] body;

    public Response() {
        header = HttpConstant.HTTP_RESPONSE_200;
    }
}
