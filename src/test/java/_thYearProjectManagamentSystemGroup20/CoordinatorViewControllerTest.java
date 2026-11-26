package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "coord", roles = "COORDINATOR")
class CoordinatorViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void clearStudents() {
        studentRepository.deleteAll();
    }

    @Test
    void dashboardShowsStudentCountsAndQuickActions() throws Exception {
        studentRepository.save(new Student("101200001", "Jane Doe", "jane@cmail.carleton.ca", "Software Engineering", true));
        studentRepository.save(new Student("101200002", "John Doe", "john@cmail.carleton.ca", "Computer Systems", false));
        studentRepository.save(new Student("101200003", "Alice Bob", "alice@cmail.carleton.ca", "Sotware Engineering", false));

        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("coordinator/dashboard"))
                .andExpect(model().attribute("totalStudents", 3L))
                .andExpect(model().attribute("unassignedCount", 2L))
                .andExpect(model().attributeExists("unassignedStudents"))
                .andExpect(content().string(containsString("Total Students")))
                .andExpect(content().string(containsString("Students without Project")))
                .andExpect(content().string(containsString("Projects")))
                .andExpect(content().string(containsString("Presentations Scheduled")))
                .andExpect(content().string(containsString("Find Students without Project")))
                .andExpect(content().string(containsString("Allocate Presentations")))
                .andExpect(content().string(containsString("Set Report Deadline")))
                .andExpect(content().string(containsString("Send Reminder Emails")));
    }

    @Test
    void dashboardWithNoStudentsShowsZeroCounts() throws Exception {
        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("coordinator/dashboard"))
                .andExpect(model().attribute("totalStudents", 0L))
                .andExpect(model().attribute("unassignedCount", 0L))
                .andExpect(model().attribute("unassignedStudents", empty()));
    }

    @Test
    void unassignedStudentsListContainsOnlyStudentsWithoutProject() throws Exception {
        studentRepository.save(new Student("101200001", "Jane Doe", "jane@cmail.carleton.ca", true));
        studentRepository.save(new Student("101200002", "John Doe", "john@cmail.carleton.ca", false));
        studentRepository.save(new Student("101200003", "Alice Bob", "alice@cmail.carleton.ca", false));

        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("unassignedStudents", containsInAnyOrder(
                        hasProperty("name", org.hamcrest.Matchers.is("John Doe")),
                        hasProperty("name", org.hamcrest.Matchers.is("Alice Bob")))));
    }

    @Test
    void allStudentsAssignedMeansNoneUnassigned() throws Exception {
        studentRepository.save(new Student("101200001", "Jane Doe", "jane@cmail.carleton.ca", true));

        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalStudents", 1L))
                .andExpect(model().attribute("unassignedCount", 0L))
                .andExpect(model().attribute("unassignedStudents", empty()));
    }

    @Test
    void dashboardRendersCountsInPage() throws Exception {
        studentRepository.save(new Student("101200002", "John Doe", "john@cmail.carleton.ca", false));

        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("unassignedStudents",
                        contains(hasProperty("studentNumber", org.hamcrest.Matchers.is("101200002")))))
                .andExpect(content().string(containsString("<div class=\"stat-value\">1</div>")));
    }

    // ====== Security ======

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/coordinator/dashboard").with(anonymous())
                        .accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }
}