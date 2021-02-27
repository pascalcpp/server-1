package com.xpcf.simpleserver.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author XPCF
 * @version 1.0
 * @date 2/27/2021 1:46 AM
 */
public class HttpUtil {



    public static final int BUFFER_SIZE = 1024;

    public static final byte[] RETURN = "\r\n".getBytes(StandardCharsets.ISO_8859_1);

    public static final byte[] DOUBLE_RETURN = "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);

    /**
     * 得到uri
     *
     * @param request
     * @return
     */
    public static String getUri(String request) {
        return StrUtil.subBetween(request, " ", " ");
    }


    public static void ParseRequest(Socket socket, InputStream inputStream) throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        int length = 0;
        String requestMethod = null;
        String uri = null;
        while ((length = inputStream.read(bytes)) > -1) {
            ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();

            String temp = new String(bytes, StandardCharsets.ISO_8859_1);
            String requestLine = StrUtil.subBefore(temp, "\r\n", false);
            String[] split = requestLine.split(" ");


            requestMethod = split[0];
            uri = split[1];

            if (requestLine.equals(HttpConstant.HTTP_REQUEST_METHOD_GET)) {

                if ((StrUtil.count(temp, "\r\n\r\n")) > 0) {
                    headerBaos.write(bytes, 0, length);
                    processGet(new String(headerBaos.toByteArray(), StandardCharsets.ISO_8859_1), inputStream, uri);
                    break;
                } else {
                    headerBaos.write(bytes, 0, length);
                    continue;
                }

            } else {

                processPost(temp, bytes, inputStream);

            }


        }
    }

    /**
     *
     * @param temp
     * @param bytes
     * @param inputStream
     */
    private static void processPost(String temp, byte[] bytes, InputStream inputStream) {

        String boundary = null;
        int contentLength = 0;
        int pos = findBytesPos(bytes, DOUBLE_RETURN);
        if (pos != -1) {
            boundary = StrUtil.subBetween(temp, "Content-Type: ", "\r\n").split(" ")[1].split("=")[1];
            contentLength = Integer.valueOf(StrUtil.subBetween(temp, "Content-Length: ", "\r\n"));
        } else {

        }
    }

    private static void processGet(String temp, InputStream inputStream, String uri) {
        File downloadFile = new File(HttpConstant.WEBFILES_DIR, uri);

    }

    /**
     * temp solution
     * @param inputStream
     * @throws IOException
     */
    public static void readRequest(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        int length = 0;
        ByteArrayOutputStream headerBaos = new ByteArrayOutputStream();
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        byte[] boundary = null;
        int contentLength = 0;
        while ((length = inputStream.read(bytes)) > -1) {
            String s1 = new String(bytes, StandardCharsets.ISO_8859_1);

            int pos = findBytesPos(bytes, DOUBLE_RETURN);

            if (pos != -1) {
                headerBaos.write(bytes, 0, pos);
                boundary = StrUtil.subBetween(headerBaos.toString(), "Content-Type: ", "\r\n").split(" ")[1].split("=")[1].getBytes(StandardCharsets.ISO_8859_1);
                contentLength = Integer.valueOf(StrUtil.subBetween(headerBaos.toString(), "Content-Length: ", "\r\n"));

                if (pos + DOUBLE_RETURN.length < length) {
                    body.write(bytes, pos + DOUBLE_RETURN.length, length - (pos + DOUBLE_RETURN.length));
                }
                while ((length = inputStream.read(bytes)) > -1) {
                    body.write(bytes, 0, length);
                    if (body.size() == contentLength) {
//                        System.out.println("");
                        break;
                    }
                }
                byte[] byteArray = body.toByteArray();
                int startPos = findBytesPos(byteArray, DOUBLE_RETURN);
                int endPos = findBytesPos(Arrays.copyOfRange(byteArray, startPos + DOUBLE_RETURN.length, body.size()), boundary);
                byte[] fileBytes = Arrays.copyOfRange(byteArray, startPos + DOUBLE_RETURN.length, endPos);

                FileUtil.writeBytes(fileBytes, new File(HttpConstant.WEBFILES_DIR, "hello.jpg"));

                break;

            } else {
                headerBaos.write(bytes, 0, length);
            }


        }


    }


    /**
     * TODO kmp
     * @param src
     * @param bytes
     * @return if return -1 not found
     *          otherwise
     */
    private static int findBytesPos(byte[] src, byte[] bytes) {
        int pos = -1;
        for (int i = 0; i <= src.length - bytes.length; i++) {
            byte[] temp = Arrays.copyOfRange(src, i, i + bytes.length);
            if (Arrays.equals(temp, bytes)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

//    private static int findBytesPosWithCount(byte[] src, byte[] bytes, int count) {
//        int pos = -1;
//        for (int i = 0; i < count; i++) {
//            pos = findBytesPos(src,bytes);
//        }
//        return pos;
//    }






}
