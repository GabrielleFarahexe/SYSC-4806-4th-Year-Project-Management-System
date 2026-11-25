package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Login and the role-based redirect from /dashboard to each portal. */
@SpringBootTest(classes = Application.class)
class HomeControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void studentIsSentToStudentDashboard() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("student").roles("STUDENT")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    void professorIsSentToProfessorDashboard() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("prof").roles("PROFESSOR")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/professor"));
    }

    @Test
    void coordinatorIsSentToCoordinatorDashboard() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("coord").roles("COORDINATOR")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/coordinator/dashboard"));
    }

    @Test
    void userWithSeveralRolesIsSentToAboutPage() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("admin").roles("STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/about"));
    }

    @Test
    void anonymousUserCannotOpenDashboard() throws Exception {
        mockMvc.perform(get("/dashboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void demoAccountsCanLogIn() throws Exception {
        mockMvc.perform(formLogin("/login").user("student").password("student123"))
                .andExpect(authenticated().withRoles("STUDENT"))
                .andExpect(redirectedUrl("/dashboard"));
        mockMvc.perform(formLogin("/login").user("professor").password("professor123"))
                .andExpect(authenticated().withRoles("PROFESSOR"));
        mockMvc.perform(formLogin("/login").user("coordinator").password("coordinator123"))
                .andExpect(authenticated().withRoles("COORDINATOR"));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(formLogin("/login").user("student").password("wrong"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }
}
