package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests: real Spring context, real H2 database with sample data,
 * security on.
 */
@SpringBootTest(classes = Application.class)
class StudentProjectControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void browsePageShowsActiveProjectsOnly() throws Exception {
        mockMvc.perform(get("/student/projects").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Autonomous Drone Navigation")))
                .andExpect(content().string(not(containsString("Legacy Course Scheduler"))));
    }

    @Test
    void browsePageFiltersByProgram() throws Exception {
        mockMvc.perform(get("/student/projects").param("program", "SOFTWARE_ENGINEERING")
                .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Accessible Transit Mobile App")))
                .andExpect(content().string(not(containsString("Low-Power Wearable Health Monitor"))));
    }

    @Test
    void dashboardLoadsForStudent() throws Exception {
        mockMvc.perform(get("/student/dashboard").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome")));
    }

    @Test
    void professorCannotOpenStudentPages() throws Exception {
        mockMvc.perform(get("/student/dashboard").with(user("prof").roles("PROFESSOR")))
                .andExpect(status().isForbidden());
    }
}