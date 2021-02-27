package com.xpcf.simpleserver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.deploy.util.StringUtils;
import com.xpcf.simpleserver.util.HttpConstant;
import com.xpcf.simpleserver.util.HttpUtil;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Test {

    public static void main(String[] args) throws InterruptedException {

//
//        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
//        ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);
//        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper();
//        upload.parseRequest(requestWrapper);
        try {
            ServerSocket serverSocket = new ServerSocket(4396);
            Socket accept = serverSocket.accept();
            HttpUtil.ParseRequest(accept, accept.getInputStream());
            accept.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
