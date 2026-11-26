package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.config.SecurityConfig;
import _thYearProjectManagamentSystemGroup20.controller.ProfessorController;
import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ProfessorController: only the web layer is loaded, and
 * ProjectRepository is a Mockito mock, so no database is involved.
 */
@WebMvcTest(ProfessorController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "PROFESSOR")
class ProfessorControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectRepository projectRepository;

    @Test
    void dashboardPutsRepositoryCountsInModel() throws Exception {
        Project recent = new Project("Drone Swarm", "Coordinate drones", Set.of(Program.SOFTWARE_ENGINEERING), 3);
        recent.setCreatedAt(LocalDateTime.of(2026, 11, 1, 10, 0));
        when(projectRepository.countByStatusNot(ProjectStatus.DELETED)).thenReturn(7L);
        when(projectRepository.countByStatus(ProjectStatus.ACTIVE)).thenReturn(4L);
        when(projectRepository.findTop5ByStatusNotOrderByCreatedAtDesc(ProjectStatus.DELETED))
                .thenReturn(List.of(recent));

        mockMvc.perform(get("/professor"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/dashboard"))
                .andExpect(model().attribute("totalProjects", 7L))
                .andExpect(model().attribute("activeProjects", 4L))
                .andExpect(model().attribute("totalApplications", 0L))
                .andExpect(model().attribute("upcomingPresentations", 0L))
                .andExpect(model().attribute("recentProjects", List.of(recent)));
    }

    @Test
    void everyViewGetsProgramList() throws Exception {
        mockMvc.perform(get("/professor/projects/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/project-form"))
                .andExpect(model().attribute("programs", Program.values()))
                .andExpect(model().attributeExists("project"));
    }

    @Test
    void createProjectIgnoresIdStatusAndCreatedAtFromForm() throws Exception {
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/professor/projects").with(csrf())
                        .param("id", "99")
                        .param("status", "DELETED")
                        .param("createdAt", "2020-01-01T00:00")
                        .param("title", "AI Tutor")
                        .param("description", "An intelligent tutoring system")
                        .param("requiredStudents", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor"))
                .andExpect(flash().attribute("successMessage", "Project \"AI Tutor\" was created."));

        ArgumentCaptor<Project> saved = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(saved.capture());
        assertThat(saved.getValue().getId()).isNull();
        assertThat(saved.getValue().getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        assertThat(saved.getValue().getCreatedAt()).isNull();
    }

    @Test
    void invalidProjectIsNotSaved() throws Exception {
        mockMvc.perform(post("/professor/projects").with(csrf())
                        .param("title", "")
                        .param("description", "")
                        .param("requiredStudents", "11"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/project-form"))
                .andExpect(model().attributeHasFieldErrors("project", "title", "description", "requiredStudents"));

        verify(projectRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotAccessProfessorPages() throws Exception {
        mockMvc.perform(get("/professor"))
                .andExpect(status().isForbidden());
    }
}
