package com.hackyle.demo.zxing.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 生成二维码
 * 1.原生二维码
 * 2.带LOGO的二维码
 * 3.带说明信息的二维码
 */
public class QRCodeUtils {
    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    /** 二维码尺寸（像素） */
    private static final int QRCODE_SIZE = 300;
    /** LOGO宽度（像素） */
    private static final int WIDTH = 60;
    /** LOGO高度（像素） */
    private static final int HEIGHT = 60;


    /**
     * 生成原生的二维码，流的形式
     * @param content 要被生成二维码的内容
     */
    public static void genQRCodeWithPlain(String content, OutputStream outputStream) throws IOException {
        BitMatrix bitMatrix = qrCodeBitMatrix(content);
        if(bitMatrix == null) {
            throw new RuntimeException("BitMatrix为空");
        }

        MatrixToImageWriter.writeToStream(bitMatrix, FORMAT_NAME, outputStream);
    }

    /**
     * 生成原生的二维码，文件的形式
     * @param content 要被生成二维码的内容
     * @param destDir 生成的二维码保存在哪里
     */
    public static void genQRCodeWithPlain(String content, File destDir) throws IOException {
        checkDestDir(destDir);

        BitMatrix bitMatrix = qrCodeBitMatrix(content);
        if(bitMatrix == null) {
            throw new RuntimeException("BitMatrix为空");
        }

        BufferedImage bufferedImage = bitMatrix2Image(bitMatrix);
        ImageIO.write(bufferedImage, FORMAT_NAME, new File(destDir+"/"+ UUID.randomUUID().toString()));
    }

    /**
     * 生成内嵌LOGO的二维码，流的形式
     * @param content 要被生成二维码的内容
     * @param logoStream LOGO图片流
     * @param outputStream 输出
     */
    public static void genQRCodeWithLogo(String content, InputStream logoStream, OutputStream outputStream) throws IOException {
        BitMatrix bitMatrix = qrCodeBitMatrix(content);
        assert bitMatrix != null;
        BufferedImage qrCodeImage = bitMatrix2Image(bitMatrix);

        Image logoImage = ImageIO.read(logoStream);
        int width = logoImage.getWidth(null);
        int height = logoImage.getHeight(null);

        // 压缩LOGO
        if (width > WIDTH) {
            width = WIDTH;
        }
        if (height > HEIGHT) {
            height = HEIGHT;
        }
        Image image = logoImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.drawImage(image, 0, 0, null); // 绘制缩小后的图
        g.dispose();
        logoImage = image;

        // 插入LOGO
        Graphics2D graph = qrCodeImage.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(logoImage, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();

        //BufferedImage转InputStream
        ImageIO.write(qrCodeImage, FORMAT_NAME, outputStream);
    }


    /**
     * 生成内嵌LOGO的二维码，文件的形式
     * @param content 要被生成二维码的内容
     * @param logoStream LOGO图片流
     * @param destDir 生成的二维码保存在哪里
     */
    public static void genQRCodeWithLogo(String content, InputStream logoStream, File destDir) throws IOException {
        checkDestDir(destDir);

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destDir+"/"+UUID.randomUUID().toString()));
        genQRCodeWithLogo(content, logoStream, outputStream);
    }


    /**
     * 生成带说明文字的二维码，流的形式
     * @param content 要被生成二维码的内容
     * @param descText 二维码的说明文本
     * @param outputStream 输出流
     */
    public static void genQRCodeWithDesc(String content, String descText, OutputStream outputStream) throws IOException {
        BitMatrix bitMatrix = qrCodeBitMatrix(content);
        assert bitMatrix != null;
        BufferedImage qrCodeImage = bitMatrix2Image(bitMatrix);

        //二维码的说明信息
        Graphics2D graph = qrCodeImage.createGraphics();
        graph.setColor(Color.RED); //文本颜色
        graph.drawString(descText,20,295); //文本位置，以BufferedImage的左上角为原点，向右为X轴，向下为Y轴
        graph.dispose();

        //BufferedImage转InputStream
        ImageIO.write(qrCodeImage, FORMAT_NAME, outputStream);
    }

    /**
     * 解析二维码
     * @param qrCodeStream 二维码文件流
     * @return 解析后的二维码
     */
    public static String decodeQRCode(InputStream qrCodeStream) throws Exception {
        BufferedImage image = ImageIO.read(qrCodeStream);
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        Result result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }


    /**
     * 字符串转BitMatrix
     * @param content 要被生成二维码的内容
     */
    private static BitMatrix qrCodeBitMatrix(String content) {
        //设置图片的文字编码以及内边框
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //容错级别
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET); //编码
        hints.put(EncodeHintType.MARGIN, 1); //边框距
        BitMatrix bitMatrix;
        try {
            //参数分别为：编码内容、编码类型、图片宽度、图片高度，设置参数
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        }catch(WriterException e) {
            e.printStackTrace();
            return null;
        }
        return bitMatrix;
    }

    /**
     * BitMatrix转Image
     */
    private static BufferedImage bitMatrix2Image(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        //将BitMatrix中的像素二进制拷贝出来，放在BufferedImage中
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    private static void checkDestDir(File destDir) {
        if(destDir == null) {
            System.out.println("记录日志：生成原生的二维码，入参不合法");
            throw new IllegalArgumentException ("生成原生的二维码，入参不合法");
        }
        if(!destDir.exists() || !destDir.isDirectory()) {
            if(!destDir.mkdirs()) {
                throw new RuntimeException("创建文件夹错误");
            }
        }
    }
}
