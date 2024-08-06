package com.example.ecommerce.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ImageUtils {

    public static byte[] compress(MultipartFile file, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        BufferedImage bufferedScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedScaledImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedScaledImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
