package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.config.SecurityConfig;
import _thYearProjectManagamentSystemGroup20.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for HomeController's routing logic. It has no dependencies, so
 * only the web layer and security config are loaded.
 */
@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rootRedirectsAnonymousUserToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void rootRedirectsLoggedInUserToDashboard() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void aboutAndLoginArePublic() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("href=\"/login\">Log in</a>")));
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void aboutLinkFromLoginOffersReturnEvenWithAnExistingSession() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(content().string(containsString("href=\"/about?from=login\"")));
        mockMvc.perform(get("/about").param("from", "login"))
                .andExpect(content().string(containsString("href=\"/login\">Log in</a>")));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void signedInVisitorDoesNotSeeLoginOnAboutPage() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("href=\"/login\""))));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentDashboardRedirect() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    @WithMockUser(roles = "PROFESSOR")
    void professorDashboardRedirect() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(redirectedUrl("/professor"));
    }

    @Test
    @WithMockUser(roles = "COORDINATOR")
    void coordinatorDashboardRedirect() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(redirectedUrl("/coordinator/dashboard"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "STUDENT", "PROFESSOR", "COORDINATOR"})
    void adminGoesToCoordinatorDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(redirectedUrl("/coordinator/dashboard"));
    }

    @Test
    void dashboardRequiresLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
