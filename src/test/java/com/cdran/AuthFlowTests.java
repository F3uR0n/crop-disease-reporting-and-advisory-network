package com.cdran;

import com.cdran.model.entity.User;
import com.cdran.model.enums.AccountStatus;
import com.cdran.model.enums.Role;
import com.cdran.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Integration tests for the foundation: registration rules + role-based access.
 * Runs against in-memory H2 (profile "h2") with the real Flyway migrations.
 */
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class AuthFlowTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void initialAdminIsCreatedFromProperties() {
        User admin = userRepository.findByPhone("01700000000").orElseThrow();
        assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void farmerRegistrationCreatesActiveFarmer() throws Exception {
        mvc.perform(post("/register").with(csrf())
                        .param("accountType", "FARMER")
                        .param("fullName", "Rahim Uddin")
                        .param("phone", "01711111111")
                        .param("preferredLanguage", "BN")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        User saved = userRepository.findByPhone("01711111111").orElseThrow();
        assertThat(saved.getRole()).isEqualTo(Role.FARMER);
        assertThat(saved.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(saved.getPasswordHash()).isNotEqualTo("secret123");
    }

    @Test
    void farmerDashboardShowsFarmLinksAndNoLongerMarksFarmFeatureComingSoon() throws Exception {
        MvcResult result = mvc.perform(get("/farmer/dashboard")
                        .with(user("01711111111").roles("FARMER")))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString())
                .contains("My farms", "Add a farm", "/farmer/farms", "/farmer/farms/new")
                .doesNotContain("F5 My fields");
    }

    @Test
    void expertRegistrationIsPendingApproval() throws Exception {
        mvc.perform(post("/register").with(csrf())
                        .param("accountType", "EXPERT")
                        .param("fullName", "Dr. Karim")
                        .param("phone", "01822222222")
                        .param("preferredLanguage", "EN")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123")
                        .param("qualification", "MSc Plant Pathology"))
                .andExpect(status().is3xxRedirection());

        User saved = userRepository.findByPhone("01822222222").orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(AccountStatus.PENDING_APPROVAL);
        assertThat(saved.isActive()).isFalse();
    }

    @Test
    void invalidPhoneAndMismatchedPasswordAreRejected() throws Exception {
        mvc.perform(post("/register").with(csrf())
                        .param("accountType", "FARMER")
                        .param("fullName", "Test")
                        .param("phone", "12345")
                        .param("preferredLanguage", "EN")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("form", "phone"));

        mvc.perform(post("/register").with(csrf())
                        .param("accountType", "FARMER")
                        .param("fullName", "Test")
                        .param("phone", "01933333333")
                        .param("preferredLanguage", "EN")
                        .param("password", "secret123")
                        .param("confirmPassword", "different1"))
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("form", "confirmPassword"));
    }

    @Test
    void duplicatePhoneIsRejected() throws Exception {
        mvc.perform(post("/register").with(csrf())
                        .param("accountType", "FARMER")
                        .param("fullName", "Someone")
                        .param("phone", "01700000000") // already used by the admin
                        .param("preferredLanguage", "EN")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("form", "phone"));
    }

    @Test
    void roleAreasAreProtected() throws Exception {
        mvc.perform(get("/farmer/dashboard"))
                .andExpect(status().is3xxRedirection()); // to login

        mvc.perform(get("/admin/dashboard").with(user("01711111111").roles("FARMER")))
                .andExpect(status().isForbidden());

        mvc.perform(get("/farmer/dashboard").with(user("01711111111").roles("FARMER")))
                .andExpect(status().isOk())
                .andExpect(view().name("farmer/dashboard"));
    }

    @Test
    void dashboardRedirectsByRole() throws Exception {
        mvc.perform(get("/dashboard").with(user("x").roles("EXPERT")))
                .andExpect(redirectedUrl("/expert/dashboard"));
        mvc.perform(get("/dashboard").with(user("x").roles("ADMIN")))
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    void banglaPagesRender() throws Exception {
        mvc.perform(get("/login").param("lang", "bn"))
                .andExpect(status().isOk());
    }
}
