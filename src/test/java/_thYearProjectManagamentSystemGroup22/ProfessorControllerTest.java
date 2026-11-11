package _thYearProjectManagamentSystemGroup22;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-context tests; @Transactional rolls the database back after each test.
 * Spring Security is on the classpath, so every request runs as a logged-in
 * mock professor (@WithMockUser) and every POST carries a CSRF token (.with(csrf())),
 * just like a real browser submitting the Thymeleaf form.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "prof", roles = "PROFESSOR")
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void clearProjects() {
        projectRepository.deleteAll();
    }

    @Test
    void dashboardShowsStatsAndRecentActivity() throws Exception {
        projectRepository.save(new Project("Drone Swarm", "Coordinate drones",
                Set.of(Program.SOFTWARE_ENGINEERING), 3));
        Project deleted = new Project("Old Topic", "No longer offered", Set.of(), 2);
        deleted.setStatus(ProjectStatus.DELETED);
        projectRepository.save(deleted);

        mockMvc.perform(get("/professor"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/dashboard"))
                .andExpect(model().attribute("totalProjects", 1L)) // deleted one not counted
                .andExpect(content().string(containsString("Drone Swarm")))
                .andExpect(content().string(not(containsString("Old Topic"))));
    }

    @Test
    void dashboardWithNoProjectsShowsEmptyMessage() throws Exception {
        mockMvc.perform(get("/professor"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalProjects", 0L))
                .andExpect(content().string(containsString("No activity yet")));
    }

    @Test
    void newProjectFormRendersAllFields() throws Exception {
        mockMvc.perform(get("/professor/projects/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/project-form"))
                .andExpect(model().attributeExists("project", "programs"))
                .andExpect(content().string(containsString("name=\"title\"")))
                .andExpect(content().string(containsString("name=\"description\"")))
                .andExpect(content().string(containsString("name=\"programRestrictions\"")))
                .andExpect(content().string(containsString("name=\"requiredStudents\"")))
                .andExpect(content().string(containsString("Software Engineering")));
    }

    @Test
    void validProjectIsSavedAndRedirectsToDashboard() throws Exception {
        mockMvc.perform(post("/professor/projects")
                        .with(csrf())
                        .param("title", "AI Tutor")
                        .param("description", "An intelligent tutoring system")
                        .param("programRestrictions", "SOFTWARE_ENGINEERING", "COMPUTER_SYSTEMS_ENGINEERING")
                        .param("requiredStudents", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/professor"))
                .andExpect(flash().attributeExists("successMessage"));

        assertThat(projectRepository.findAll()).singleElement().satisfies(p -> {
            assertThat(p.getTitle()).isEqualTo("AI Tutor");
            assertThat(p.getRequiredStudents()).isEqualTo(4);
            assertThat(p.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
            assertThat(p.getCreatedAt()).isNotNull();
            assertThat(p.getProgramRestrictions()).containsExactlyInAnyOrder(
                    Program.SOFTWARE_ENGINEERING, Program.COMPUTER_SYSTEMS_ENGINEERING);
        });
    }

    @Test
    void projectWithNoRestrictionsIsOpenToAll() throws Exception {
        mockMvc.perform(post("/professor/projects")
                        .with(csrf())
                        .param("title", "Open Topic")
                        .param("description", "Anyone can apply")
                        .param("requiredStudents", "2"))
                .andExpect(status().is3xxRedirection());

        assertThat(projectRepository.findAll()).singleElement()
                .satisfies(p -> assertThat(p.isOpenToAllPrograms()).isTrue());
    }

    @Test
    void invalidProjectReshowsFormWithErrors() throws Exception {
        mockMvc.perform(post("/professor/projects")
                        .with(csrf())
                        .param("title", "")
                        .param("description", "")
                        .param("requiredStudents", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/project-form"))
                .andExpect(model().attributeHasFieldErrors("project", "title", "description", "requiredStudents"))
                .andExpect(content().string(containsString("Title is required")));

        assertThat(projectRepository.count()).isZero();
    }
}