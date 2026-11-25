package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.config.SecurityConfig;
import _thYearProjectManagamentSystemGroup20.controller.CoordinatorViewController;
import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CoordinatorViewController: only the web layer is loaded, and
 * StudentRepository is a Mockito mock.
 */
@WebMvcTest(CoordinatorViewController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "COORDINATOR")
class CoordinatorViewControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    @Test
    void dashboardPutsStudentCountsInModel() throws Exception {
        List<Student> unassigned = List.of(
                new Student("101200002", "John Doe", "john@cmail.carleton.ca", false));
        when(studentRepository.count()).thenReturn(3L);
        when(studentRepository.countByAssigned(false)).thenReturn(1L);
        when(studentRepository.findByAssigned(false)).thenReturn(unassigned);

        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("coordinator/dashboard"))
                .andExpect(model().attribute("totalStudents", 3L))
                .andExpect(model().attribute("unassignedCount", 1L))
                .andExpect(model().attribute("unassignedStudents", unassigned));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotAccessCoordinatorDashboard() throws Exception {
        mockMvc.perform(get("/coordinator/dashboard"))
                .andExpect(status().isForbidden());
    }
}
