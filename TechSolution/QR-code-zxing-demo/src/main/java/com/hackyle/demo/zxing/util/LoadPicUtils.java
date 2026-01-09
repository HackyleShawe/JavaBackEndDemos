package com.hackyle.demo.zxing.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * 加载图片资源，返回图片流
 * 1.从文件中载入图片流
 * 2.从网络上载入图片流
 */
public class LoadPicUtils {
    /**
     * 从文件载入图片的文件流
     * @param file 文件File
     * @return 文件字节流
     */
    public static InputStream loadLogoByFile(File file) throws IOException {
        if(file == null || !file.exists() || !file.isFile()) {
            System.out.println("记录日志：加载图片文件流失败，入参不合法");
            throw new IllegalArgumentException ("加载图片文件流失败，入参不合法");
        }

        return new BufferedInputStream(new FileInputStream(file));
    }

    /**
     * 从网络上载入图片的文件流
     * @param url 的网络地址
     * @return 文件流
     */
    public static InputStream loadLogoByURL(String url) throws IOException {
        //入参校验
        if(url == null || "".equals(url.trim())) {
            System.out.println("记录日志：加载网络上图片的失败，入参不合法");
            throw new IllegalArgumentException ("加载网络上图片的失败，入参不合法");
        }

        //定义返回值
        InputStream resultInputStream = null;

        //支持多种协议：http, https, ftp, file
        //支持中文的括号：（）
        //支持中文：\u4e00-\u9fa5
        String reg = "^(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;（）\\u4e00-\\u9fa5]+[-A-Za-z0-9+&@#/%=~_|（）\\u4e00-\\u9fa5]$";
        Pattern pattern = Pattern.compile(reg);
        if(!pattern.matcher(url).find()) {
            System.out.println("记录日志：图片的URL不合法");
            throw new IllegalArgumentException ("图片的URL不合法");
        }

        /* 1. 得到访问地址的URL */
        URL urlInstance = new URL(url);
        /* 2. 得到网络访问对象java.net.HttpURLConnection */
        HttpURLConnection connection = (HttpURLConnection) urlInstance.openConnection();
        /* 3. 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接 */
        // 设置是否向HttpURLConnection输出
        connection.setDoOutput(false);
        // 设置是否从HttpUrlConnection读入
        connection.setDoInput(true);
        // 设置请求方式
        connection.setRequestMethod("GET");
        // 设置是否使用缓存
        connection.setUseCaches(true);
        // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
        connection.setInstanceFollowRedirects(true);
        // 设置超时时间
        connection.setConnectTimeout(3000);
        // 连接
        connection.connect();
        /* 4. 得到响应状态码的返回值 responseCode */
        int code = connection.getResponseCode();
        /* 5. 如果返回值正常，数据在网络中是以流的形式得到服务端返回的数据 */
        if (code == 200) { // 正常响应
            // 从流中读取响应信息
            resultInputStream = connection.getInputStream();
        } else {
            System.out.println("记录日志：图片的URL连接失败");
            // 6. 断开连接，释放资源
            connection.disconnect();
            throw new RuntimeException("图片的URL连接失败");
        }

        return resultInputStream;
    }

}
