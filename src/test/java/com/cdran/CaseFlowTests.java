package com.cdran;

import com.cdran.exception.BusinessRuleException;
import com.cdran.model.entity.DiseaseCase;
import com.cdran.model.entity.Farmer;
import com.cdran.model.enums.CaseStatus;
import com.cdran.model.enums.Language;
import com.cdran.repository.DiseaseCaseRepository;
import com.cdran.repository.NotificationRepository;
import com.cdran.repository.UserRepository;
import com.cdran.service.CaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Integration tests for Feature 1 (case submission) and Feature 4 (case tracking).
 * In-memory H2 database; uploaded test images go to target/test-uploads.
 */
@SpringBootTest(properties = "app.upload.dir=target/test-uploads")
@ActiveProfiles("h2")
@Transactional
class CaseFlowTests {

    private static final String FARMER_PHONE = "01711111111";
    private static final String OTHER_FARMER_PHONE = "01722222222";

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DiseaseCaseRepository caseRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private CaseService caseService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        userRepository.save(new Farmer("Rahim", FARMER_PHONE, null, "x", Language.EN));
        userRepository.save(new Farmer("Karim", OTHER_FARMER_PHONE, null, "x", Language.EN));
    }

    /** A valid submission from Dhaka. */
    private MockMultipartHttpServletRequestBuilder validCase(byte[] imageBytes) {
        return caseAt(imageBytes, "23.8103");
    }

    private MockMultipartHttpServletRequestBuilder caseAt(byte[] imageBytes, String latitude) {
        MockMultipartHttpServletRequestBuilder request = multipart("/farmer/cases");
        request.file(new MockMultipartFile("images", "leaf.png", "image/png", imageBytes));
        request.param("cropId", "1")
                .param("growthStage", "VEGETATIVE")
                .param("symptomIds", "1", "2")
                .param("latitude", latitude)
                .param("longitude", "90.4125")
                .with(user(FARMER_PHONE).roles("FARMER"))
                .with(csrf());
        return request;
    }

    private byte[] sharpPng() {
        return TestImages.png(TestImages.sharp(600, 400));
    }

    @Test
    void validCaseIsSavedAsSubmittedWithHistoryAndNotification() throws Exception {
        MvcResult result = mvc.perform(validCase(sharpPng()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        List<DiseaseCase> cases = caseRepository.findByFarmerPhoneOrderByCreatedAtDesc(FARMER_PHONE);
        assertThat(cases).hasSize(1);
        DiseaseCase saved = caseService.getForFarmer(cases.get(0).getId(), FARMER_PHONE);

        assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/farmer/cases/" + saved.getId());
        assertThat(saved.getStatus()).isEqualTo(CaseStatus.SUBMITTED);
        assertThat(saved.getHistory()).hasSize(1);
        assertThat(saved.getImages()).hasSize(1);
        assertThat(saved.getImages().get(0).getFilePath()).startsWith("cases/");
        assertThat(saved.getSymptoms()).hasSize(2);
        assertThat(notificationRepository.countByUserPhoneAndReadAtIsNull(FARMER_PHONE)).isEqualTo(1);
    }

    @Test
    void blurryImageIsRejectedWithFormError() throws Exception {
        mvc.perform(validCase(TestImages.png(TestImages.blurry(600, 400))))
                .andExpect(status().isOk())
                .andExpect(view().name("cases/new"))
                .andExpect(model().attributeHasFieldErrors("form", "images"));

        assertThat(caseRepository.count()).isZero();
    }

    @Test
    void caseNeedsASymptomOrDescription() throws Exception {
        MockMultipartHttpServletRequestBuilder request = multipart("/farmer/cases");
        request.file(new MockMultipartFile("images", "leaf.png", "image/png", sharpPng()));
        mvc.perform(request.param("cropId", "1").param("growthStage", "VEGETATIVE")
                        .param("latitude", "23.8").param("longitude", "90.4")
                        .with(user(FARMER_PHONE).roles("FARMER")).with(csrf()))
                .andExpect(view().name("cases/new"))
                .andExpect(model().attributeHasFieldErrors("form", "symptomIds"));
    }

    @Test
    void locationOutsideBangladeshIsRejected() throws Exception {
        mvc.perform(caseAt(sharpPng(), "51.5")) // London
                .andExpect(view().name("cases/new"))
                .andExpect(model().attributeHasFieldErrors("form", "latitude"));
    }

    @Test
    void farmerCannotOpenAnotherFarmersCase() throws Exception {
        mvc.perform(validCase(sharpPng()));
        Long caseId = caseRepository.findByFarmerPhoneOrderByCreatedAtDesc(FARMER_PHONE).get(0).getId();

        mvc.perform(get("/farmer/cases/" + caseId).with(user(FARMER_PHONE).roles("FARMER")))
                .andExpect(status().isOk())
                .andExpect(view().name("cases/detail"));

        mvc.perform(get("/farmer/cases/" + caseId).with(user(OTHER_FARMER_PHONE).roles("FARMER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void statusChangeAddsHistoryAndNotificationAndIllegalChangeIsRefused() throws Exception {
        mvc.perform(validCase(sharpPng()));
        Long caseId = caseRepository.findByFarmerPhoneOrderByCreatedAtDesc(FARMER_PHONE).get(0).getId();

        caseService.changeStatus(caseId, CaseStatus.UNDER_REVIEW, "Low AI confidence");

        DiseaseCase c = caseService.getForFarmer(caseId, FARMER_PHONE);
        assertThat(c.getStatus()).isEqualTo(CaseStatus.UNDER_REVIEW);
        assertThat(c.getHistory()).hasSize(2);
        assertThat(c.hasReached(CaseStatus.AI_ANALYZED)).isFalse();
        assertThat(notificationRepository.countByUserPhoneAndReadAtIsNull(FARMER_PHONE)).isEqualTo(2);

        // UNDER_REVIEW -> SUBMITTED is not an allowed move
        assertThatThrownBy(() -> caseService.changeStatus(caseId, CaseStatus.SUBMITTED, null))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void trackingPagesRender() throws Exception {
        mvc.perform(validCase(sharpPng()));

        mvc.perform(get("/farmer/dashboard").with(user(FARMER_PHONE).roles("FARMER")))
                .andExpect(status().isOk());
        mvc.perform(get("/farmer/cases").with(user(FARMER_PHONE).roles("FARMER")).param("lang", "bn"))
                .andExpect(status().isOk());
        mvc.perform(get("/farmer/cases/new").with(user(FARMER_PHONE).roles("FARMER")))
                .andExpect(status().isOk());
        mvc.perform(get("/notifications").with(user(FARMER_PHONE).roles("FARMER")))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/cases").with(user("01700000000").roles("ADMIN")))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/cases").with(user(FARMER_PHONE).roles("FARMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void openingNotificationMarksItReadAndGoesToCase() throws Exception {
        mvc.perform(validCase(sharpPng()));
        Long caseId = caseRepository.findByFarmerPhoneOrderByCreatedAtDesc(FARMER_PHONE).get(0).getId();
        Long notificationId = notificationRepository.findByUserPhoneOrderByCreatedAtDesc(FARMER_PHONE).get(0).getId();

        mvc.perform(post("/notifications/" + notificationId + "/open")
                        .with(user(FARMER_PHONE).roles("FARMER")).with(csrf()))
                .andExpect(redirectedUrl("/farmer/cases/" + caseId));

        assertThat(notificationRepository.countByUserPhoneAndReadAtIsNull(FARMER_PHONE)).isZero();
    }
}
