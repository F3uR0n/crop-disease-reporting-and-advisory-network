package com.cdran;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/** Generates small test pictures in memory, so tests need no image files. */
public final class TestImages {

    private TestImages() {
    }

    /** Random black/white pixels: lots of edges, so it counts as very sharp. */
    public static BufferedImage sharp(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Random random = new Random(42);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, random.nextBoolean() ? 0xFFFFFF : 0x000000);
            }
        }
        return image;
    }

    /** A smooth left-to-right gray gradient: no edges, so it counts as blurry. */
    public static BufferedImage blurry(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = x * 255 / width;
                image.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
            }
        }
        return image;
    }

    public static byte[] png(BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
