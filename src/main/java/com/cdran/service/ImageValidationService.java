package com.cdran.service;

import com.cdran.dto.ImageCheckResult;
import com.cdran.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.Set;

/**
 * Image quality check for case photos (Feature 1). Rejects, in this order:
 * 1. wrong file type (only JPEG / PNG),
 * 2. files larger than 5 MB,
 * 3. images narrower than app.image.min-width pixels,
 * 4. blurry images (sharpness below app.image.min-sharpness).
 */
@Service
public class ImageValidationService {

    static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");

    /** Images are shrunk to this width before measuring sharpness, so big and small photos are judged alike. */
    private static final int SHARPNESS_SAMPLE_WIDTH = 800;

    private final int minWidth;
    private final double minSharpness;

    public ImageValidationService(@Value("${app.image.min-width:400}") int minWidth,
                                  @Value("${app.image.min-sharpness:100}") double minSharpness) {
        this.minWidth = minWidth;
        this.minSharpness = minSharpness;
    }

    public ImageCheckResult validate(MultipartFile file) {
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessRuleException("images", "case.error.imageType");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new BusinessRuleException("images", "case.error.imageSize");
        }

        BufferedImage image = read(file);
        if (image == null) { // the bytes are not a real image
            throw new BusinessRuleException("images", "case.error.imageType");
        }
        if (image.getWidth() < minWidth) {
            throw new BusinessRuleException("images", "case.error.imageSmall");
        }

        double sharpness = sharpness(image);
        if (sharpness < minSharpness) {
            throw new BusinessRuleException("images", "case.error.imageBlurry");
        }
        return new ImageCheckResult(image.getWidth(), image.getHeight(), sharpness);
    }

    /**
     * Blur detection with the "variance of the Laplacian" method.
     * The Laplacian of a pixel is 4 x itself minus its 4 neighbours: large at sharp edges, near 0 on smooth areas.
     * A sharp photo has many strong edges, so these values vary a lot (high variance).
     * A blurry photo has soft edges, so the values stay close to 0 (low variance).
     */
    double sharpness(BufferedImage image) {
        // Step 1: shrink (if wider than the sample width) and convert to grayscale in one drawing step.
        int width = Math.min(image.getWidth(), SHARPNESS_SAMPLE_WIDTH);
        int height = Math.max(1, image.getHeight() * width / image.getWidth());
        BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        Raster pixels = gray.getRaster();

        // Step 2: Laplacian of every inner pixel, collecting sum and sum of squares.
        double sum = 0;
        double sumOfSquares = 0;
        long count = 0;
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int laplacian = 4 * pixels.getSample(x, y, 0)
                        - pixels.getSample(x - 1, y, 0) - pixels.getSample(x + 1, y, 0)
                        - pixels.getSample(x, y - 1, 0) - pixels.getSample(x, y + 1, 0);
                sum += laplacian;
                sumOfSquares += (double) laplacian * laplacian;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }

        // Step 3: variance = mean of squares - square of mean.
        double mean = sum / count;
        return sumOfSquares / count - mean * mean;
    }

    private BufferedImage read(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }
}
