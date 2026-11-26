package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.config.SecurityConfig;
import _thYearProjectManagamentSystemGroup20.controller.StudentProjectController;
import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for StudentProjectController: only the web layer is loaded, and
 * ProjectService is a Mockito mock.
 */
@WebMvcTest(StudentProjectController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "STUDENT")
class StudentProjectControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    private static Project project(String title, int students) {
        return new Project(title, "Description of " + title, Set.of(), students);
    }

    @Test
    void dashboardSumsSpotsAndShowsFirstThreeProjects() throws Exception {
        Project a = project("A", 2);
        Project b = project("B", 3);
        Project c = project("C", 4);
        Project d = project("D", 5);
        when(projectService.getActiveProjects()).thenReturn(List.of(a, b, c, d));
        when(projectService.getProgramsInUse())
                .thenReturn(Set.of(Program.SOFTWARE_ENGINEERING, Program.ELECTRICAL_ENGINEERING));

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"))
                .andExpect(model().attribute("totalProjects", 4))
                .andExpect(model().attribute("totalPrograms", 2))
                .andExpect(model().attribute("totalSpots", 14))
                .andExpect(model().attribute("featuredProjects", List.of(a, b, c)));
    }

    @Test
    void dashboardHandlesNoProjects() throws Exception {
        when(projectService.getActiveProjects()).thenReturn(List.of());
        when(projectService.getProgramsInUse()).thenReturn(Set.of());

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalProjects", 0))
                .andExpect(model().attribute("totalSpots", 0))
                .andExpect(model().attribute("featuredProjects", List.of()));
    }

    @Test
    void projectsPassesSearchParamsToService() throws Exception {
        Project match = project("Drone", 3);
        when(projectService.searchProjects("drone", "SOFTWARE_ENGINEERING")).thenReturn(List.of(match));

        mockMvc.perform(get("/student/projects")
                        .param("keyword", "drone")
                        .param("program", "SOFTWARE_ENGINEERING"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/projects"))
                .andExpect(model().attribute("projects", List.of(match)))
                .andExpect(model().attribute("programs", Program.values()))
                .andExpect(model().attribute("keyword", "drone"))
                .andExpect(model().attribute("selectedProgram", "SOFTWARE_ENGINEERING"));

        verify(projectService).searchProjects("drone", "SOFTWARE_ENGINEERING");
    }

    @Test
    void projectsWithoutParamsSearchesWithNulls() throws Exception {
        when(projectService.searchProjects(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/student/projects"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projects", List.of()));

        verify(projectService).searchProjects(null, null);
    }

    @Test
    @WithMockUser(roles = "PROFESSOR")
    void professorCannotAccessStudentPages() throws Exception {
        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isForbidden());
    }
}
