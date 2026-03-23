package com.croissant.CroissantClicker;

import javax.swing.*;
import java.awt.*;

public class ImageIconLoader {

    private ImageIconLoader() {}

    public static ImageIcon loadIcon(String filePath){
        var url = ImageIconLoader.class.getResource("/assets/" + filePath);

        if (url == null) {
            throw new IllegalArgumentException("Non-existent icon asset: " + filePath);
        }

        return new ImageIcon(url);
    }

    public static ImageIcon loadIcon(String filePath, int size){
        ImageIcon icon = loadIcon(filePath);
        Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }
}
