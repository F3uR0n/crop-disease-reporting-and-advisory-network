package com.cdran.service;

import com.cdran.TestImages;
import com.cdran.dto.ImageCheckResult;
import com.cdran.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Unit tests for the Feature 1 image quality rules (no Spring context needed). */
class ImageValidationServiceTests {

    private final ImageValidationService service = new ImageValidationService(400, 100);

    private static MockMultipartFile file(String contentType, byte[] bytes) {
        return new MockMultipartFile("images", "leaf.png", contentType, bytes);
    }

    @Test
    void sharpImageIsAccepted() {
        ImageCheckResult result = service.validate(file("image/png", TestImages.png(TestImages.sharp(600, 400))));

        assertThat(result.width()).isEqualTo(600);
        assertThat(result.height()).isEqualTo(400);
        assertThat(result.sharpness()).isGreaterThan(100);
    }

    @Test
    void blurryImageIsRejected() {
        assertThatThrownBy(() -> service.validate(file("image/png", TestImages.png(TestImages.blurry(600, 400)))))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("case.error.imageBlurry");
    }

    @Test
    void imageNarrowerThanMinimumIsRejected() {
        // boundary: 399 px fails, 400 px passes
        assertThatThrownBy(() -> service.validate(file("image/png", TestImages.png(TestImages.sharp(399, 400)))))
                .hasMessage("case.error.imageSmall");
        assertThat(service.validate(file("image/png", TestImages.png(TestImages.sharp(400, 400))))).isNotNull();
    }

    @Test
    void wrongFileTypeIsRejected() {
        assertThatThrownBy(() -> service.validate(file("application/pdf", new byte[]{1, 2, 3})))
                .hasMessage("case.error.imageType");
        // says PNG but the bytes are not an image
        assertThatThrownBy(() -> service.validate(file("image/png", new byte[]{1, 2, 3})))
                .hasMessage("case.error.imageType");
    }

    @Test
    void fileOver5MbIsRejected() {
        byte[] tooBig = new byte[(int) ImageValidationService.MAX_BYTES + 1];
        assertThatThrownBy(() -> service.validate(file("image/jpeg", tooBig)))
                .hasMessage("case.error.imageSize");
    }
}
