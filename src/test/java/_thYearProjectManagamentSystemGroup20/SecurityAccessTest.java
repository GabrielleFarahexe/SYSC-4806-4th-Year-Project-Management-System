package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

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
                .andExpect(status().isOk());
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
                .andExpect(status().isOk());
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
                .andExpect(status().isOk());
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
    void adminCanAccessAllDashboards() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                        .with(user("admin")
                                .roles("STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/professor")
                        .with(user("admin")
                                .roles("STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/coordinator/dashboard")
                        .with(user("admin")
                                .roles("STUDENT", "PROFESSOR", "COORDINATOR")))
                .andExpect(status().isOk());
    }

}
