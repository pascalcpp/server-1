package com.xpcf.simpleserver.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.io.FileUtils;

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

            if (requestMethod.equals(HttpConstant.HTTP_REQUEST_METHOD_GET)) {

                if ((StrUtil.count(temp, "\r\n\r\n")) > 0) {
                    headerBaos.write(bytes, 0, length);
                    processGet(new String(headerBaos.toByteArray(), StandardCharsets.ISO_8859_1), inputStream, uri, socket);
                    break;
                } else {
                    headerBaos.write(bytes, 0, length);
                    continue;
                }

            } else {
                processPost(temp, bytes, inputStream, socket, length);
                break;
            }

        }
//        inputStream.close();
    }

    /**
     * @param temp
     * @param bytes
     * @param inputStream
     */
    private static void processPost(String temp, byte[] bytes, InputStream inputStream, Socket socket, int length) throws IOException {
        ByteArrayOutputStream requestBuffer = new ByteArrayOutputStream();

        String boundary = null;

        int contentLength = 0;

        requestBuffer.write(bytes, 0, length);

        byte[] postBuffer = new byte[BUFFER_SIZE];

        int readLength = 0;

        while ((readLength = inputStream.read(postBuffer)) > -1) {

            int pos = findBytesPos(requestBuffer.toByteArray(), DOUBLE_RETURN);
            if (pos != -1) {
                contentLength = Integer.valueOf(StrUtil.subBetween(temp, "Content-Length: ", "\r\n"));
                boundary = StrUtil.subBetween(temp, "Content-Type: ", "\r\n").split(" ")[1].split("=")[1];
                byte[] contentBytes = new byte[contentLength];
                ByteArrayOutputStream bodyBaos = new ByteArrayOutputStream();
                bodyBaos.write(requestBuffer.toByteArray(), pos + DOUBLE_RETURN.length, (requestBuffer.size() - (pos + DOUBLE_RETURN.length)));
                bodyBaos.write(postBuffer, 0, readLength);

                if (bodyBaos.size() == contentLength) {
                    processPostBody(bodyBaos.toByteArray(), boundary);
                } else {

                    while ((readLength = inputStream.read(postBuffer)) > -1) {
                        bodyBaos.write(postBuffer, 0, readLength);
                        if (bodyBaos.size() == contentLength) {
                            break;
                        }
                    }
                    processPostBody(bodyBaos.toByteArray(), boundary);
                    break;

                }


            } else {
                requestBuffer.write(postBuffer, 0, readLength);
            }
        }

        OutputStream outputStream = socket.getOutputStream();
        String responseHeader = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n\r\n";
        String responseText = "upload success";

        outputStream.write((responseHeader + responseText).getBytes(StandardCharsets.ISO_8859_1));
//        outputStream.flush();
//        outputStream.close();
    }

    private static void processPostBody(byte[] bytes, String boundary) {
        String content = new String(bytes, StandardCharsets.ISO_8859_1);
        String fileName = StrUtil.subBetween(content, "Content-Disposition: ", "\r\n")
                .split(" ")[2]
                .split("=")[1]
                .replace("\"", "");
        String realBody = StrUtil.subBetween(content, "\r\n\r\n", "--" + boundary + "--");
        byte[] bodyBytes = realBody.getBytes(StandardCharsets.ISO_8859_1);
        File file = new File(HttpConstant.WEBFILES_DIR, fileName);
        FileUtil.writeBytes(bodyBytes, file);

    }

    /**
     * TODO other
     *
     * @param temp
     * @param inputStream
     * @param uri
     * @param socket
     * @throws IOException
     */
    private static void processGet(String temp, InputStream inputStream, String uri, Socket socket) throws IOException {

        OutputStream outputStream = socket.getOutputStream();

        if ("/".equals(uri) || "/favicon.ico".equals(uri)) {

            String responseHeader = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n\r\n";
            File downloadFile = new File(HttpConstant.WEBFILES_DIR, "upload.html");
            byte[] fileBytes = FileUtil.readBytes(downloadFile);
            String text = new String(fileBytes, StandardCharsets.ISO_8859_1);
            outputStream.write((responseHeader + text).getBytes(StandardCharsets.ISO_8859_1));
        } else {
            File downloadFile = new File(HttpConstant.WEBFILES_DIR, uri);
            byte[] fileBytes = FileUtil.readBytes(downloadFile);

            byte[] response200Bytes = HttpConstant.HTTP_RESPONSE_200.getBytes(StandardCharsets.ISO_8859_1);

            byte[] responseBytes = new byte[response200Bytes.length + fileBytes.length];

            ArrayUtil.copy(response200Bytes, 0, responseBytes, 0, response200Bytes.length);
            ArrayUtil.copy(fileBytes, 0, responseBytes, response200Bytes.length, fileBytes.length);
            outputStream.write(responseBytes);
        }


    }

    /**
     * temp solution
     *
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
     *
     * @param src
     * @param bytes
     * @return if return -1 not found
     * otherwise
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
