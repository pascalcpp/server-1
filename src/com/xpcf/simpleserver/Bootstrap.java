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
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4396);
            while (true) {

                Socket accept = serverSocket.accept();
                InputStream inputStream = accept.getInputStream();
                byte[] bytes = readBytes(inputStream);
                String requestString = new String(bytes, "utf-8");
                String uri = HttpUtil.getUri(requestString);
                System.out.println(new String(bytes));
                System.out.println(uri);
                if (!StrUtil.isEmpty(uri)) {

                    File uploadFile = new File(HttpConstant.WEBFILES_DIR, StrUtil.removePrefix(uri, "/"));
                    if (FileUtil.exist(uploadFile)) {
                        // generate response
                        byte[] body = FileUtil.readBytes(uploadFile);
                        byte[] header = HttpConstant.HTTP_RESPONSE_200.getBytes();
                        byte[] response = new byte[header.length + body.length];
                        ArrayUtil.copy(header, 0, response, 0, header.length);
                        ArrayUtil.copy(body, 0, response, header.length, body.length);

                        OutputStream outputStream = accept.getOutputStream();
                        System.out.println("response data: " + new String(response));
                        outputStream.write(response);
                        outputStream.flush();
                    }

                }
                accept.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException, InterruptedException {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int length = 0;
//        while ((length = inputStream.read(buffer)) > -1) {
//          baos.write(buffer, 0, length);
//        }
        while (true) {

            int length = inputStream.read(buffer);
            if (-1 == length)
                break;
            baos.write(buffer, 0, length);
            // data not complete bug
//            if (length != bufferSize)
//                break;
        }

        return baos.toByteArray();
    }
}
