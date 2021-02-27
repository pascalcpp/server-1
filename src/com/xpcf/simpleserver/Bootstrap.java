package com.xpcf.simpleserver;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.xpcf.simpleserver.util.HttpConstant;
import com.xpcf.simpleserver.util.HttpUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author XPCF
 * @version 1.0
 * @date 2/26/2021 4:10 PM
 */
public class Bootstrap {
    public static void main(String[] args) throws InterruptedException {

//
//        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
//        ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
//        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper();
//        upload.parseRequest(requestWrapper);
        try {
            ServerSocket serverSocket = new ServerSocket(4396);
            while (true) {
                Socket accept = serverSocket.accept();
                try {
                    HttpUtil.ParseRequest(accept, accept.getInputStream());
                } catch (Exception e) {
                    System.out.println("unknown error");
                    e.printStackTrace();
                } finally {
                    accept.close();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


