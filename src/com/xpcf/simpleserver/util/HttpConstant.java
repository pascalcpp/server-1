package com.xpcf.simpleserver.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;

/**
 * @author XPCF
 * @version 1.0
 * @date 2/27/2021 1:42 AM
 */
public class HttpConstant {
    public static final File WEBFILES_DIR = new File(System.getProperty("user.dir"), "webFiles");

    public static final String HTTP_RESPONSE_200 = "HTTP/1.1 200 OK\r\n" +
//            "Server: hello/1.12.2\r\n" +
            "Content-Type: application/octet-stream\r\n\r\n";

    public static final String HTTP_REQUEST_METHOD_GET = "GET";

    public static final String HTTP_REQUEST_METHOD_POST = "POST";

}
