package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@Transactional
class SecurityAccessTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void studentCanAccessStudentDashboard() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Dashboard roles"))));
    }

    @Test
    void studentCannotAccessProfessorDashboard() throws Exception {
        mockMvc.perform(get("/professor")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentCannotAccessCoordinatorDashboard() throws Exception {
        mockMvc.perform(get("/coordinator/dashboard")
                        .with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void professorCanAccessProfessorDashboard() throws Exception {
        mockMvc.perform(get("/professor")
                        .with(user("professor").roles("PROFESSOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Dashboard roles"))));
    }

    @Test
    void professorCannotAccessStudentDashboard() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                        .with(user("professor").roles("PROFESSOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void professorCannotAccessCoordinatorDashboard() throws Exception {
        mockMvc.perform(get("/coordinator/dashboard")
                        .with(user("professor").roles("PROFESSOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void coordinatorCanAccessCoordinatorDashboard() throws Exception {
        mockMvc.perform(get("/coordinator/dashboard")
                        .with(user("coordinator").roles("COORDINATOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Dashboard roles"))));
    }

    @Test
    void coordinatorCannotAccessStudentDashboard() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                        .with(user("coordinator").roles("COORDINATOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void coordinatorCannotAccessProfessorDashboard() throws Exception {
        mockMvc.perform(get("/professor")
                        .with(user("coordinator").roles("COORDINATOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminLoginShowsDashboardSwitcherOnAvailability() throws Exception {
        var login = mockMvc.perform(formLogin().user("admin").password("group20demo"))
                .andExpect(redirectedUrl("/dashboard"))
                .andReturn();
        var session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(get("/dashboard").session(session))
                .andExpect(redirectedUrl("/coordinator/dashboard"));

        mockMvc.perform(get("/student/availability").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("aria-label=\"Switch dashboard\"")))
                .andExpect(content().string(containsString("href=\"/coordinator/dashboard\">Coordinator Dashboard")))
                .andExpect(content().string(containsString("href=\"/professor\">Professor")))
                .andExpect(content().string(matchesPattern(
                        "(?s).*href=\"/student/dashboard\"\\s+aria-current=\"true\">Student.*")));
    }

    @Test
    void existingAdminSessionCanSwitchDashboards() throws Exception {
        mockMvc.perform(get("/student/availability")
                        .with(user("admin").roles("STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Switch dashboard")));
    }

    @Test
    void aboutKeepsTheSelectedDashboardNavigation() throws Exception {
        for (String role : new String[]{"student", "professor", "coordinator"}) {
            mockMvc.perform(get("/" + role + "/about")
                            .with(user("admin").roles("ADMIN", "STUDENT", "PROFESSOR", "COORDINATOR")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(matchesPattern(
                            "(?s).*href=\"/" + role + "/about\"\\s+aria-current=\"page\".*")))
                    .andExpect(content().string(not(containsString("Sign in"))));
        }

        mockMvc.perform(get("/professor/about").with(user("student").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAllDashboards() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                        .with(user("admin")
                                .roles("ADMIN", "STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dashboard roles")));

        mockMvc.perform(get("/professor")
                        .with(user("admin")
                                .roles("ADMIN", "STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dashboard roles")));

        mockMvc.perform(get("/coordinator/dashboard")
                        .with(user("admin")
                                .roles("ADMIN", "STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome, admin")));
           }

}
