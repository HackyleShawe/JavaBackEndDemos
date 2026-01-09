package com.hackyle.demo.zxing.controller;

import com.hackyle.demo.zxing.util.LoadPicUtils;
import com.hackyle.demo.zxing.util.QRCodeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RequestMapping("/qr")
@RestController
public class QRCodeController {

    @GetMapping("/genQRCodeWithPlain")
    public void genQRCodeWithPlain(HttpServletResponse response) throws IOException {
        //给相应添加头部信息，主要告诉浏览器返回的是图片流
        response.setHeader("Cache-Control", "no-store");
        // 不设置缓存
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        QRCodeUtils.genQRCodeWithPlain("https://blog.hackyle.com", response.getOutputStream());
    }

    @GetMapping("/genQRCodeWithLogo")
    public void hello(HttpServletResponse response) throws IOException {
        //给相应添加头部信息，主要告诉浏览器返回的是图片流
        response.setHeader("Cache-Control", "no-store");
        // 不设置缓存
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        //加载项目下的pics文件夹中的文件
        InputStream inputStream = LoadPicUtils.loadLogoByFile(new File("pic/logo.png"));
        QRCodeUtils.genQRCodeWithLogo("https://blog.hackyle.com", inputStream, response.getOutputStream());
    }

    @GetMapping("/genQRCodeWithDesc")
    public void genQRCodeWithDesc(HttpServletResponse response) throws IOException {
        //给相应添加头部信息，主要告诉浏览器返回的是图片流
        response.setHeader("Cache-Control", "no-store");
        // 不设置缓存
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        QRCodeUtils.genQRCodeWithDesc("https://blog.hackyle.com", "这是二维码的说明信息", response.getOutputStream());
    }

    @GetMapping("/decodeQRCode")
    public String decodeQRCode() throws Exception {
        ByteArrayOutputStream outputTemp = new ByteArrayOutputStream(); //Output流临时存放处

        OutputStream outputStream = new BufferedOutputStream(outputTemp);
        QRCodeUtils.genQRCodeWithDesc("https://blog.hackyle.com", "这是二维码的说明信息", outputStream);

        InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(outputTemp.toByteArray()));
        String decodeQRCode = QRCodeUtils.decodeQRCode(inputStream);

        return "解析后的二维码结果为：" + decodeQRCode;
    }

}
