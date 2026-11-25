package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Integration tests for the student dashboard and Browse Projects page.
 * Each test starts from an empty project table and saves its own data, so the
 * tests don't depend on SampleDataLoader. @Transactional rolls everything back
 * afterwards.
 */
@SpringBootTest(classes = Application.class)
@Transactional
class StudentProjectControllerTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProjectRepository projectRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        projectRepository.deleteAll();
    }

    private Project save(String title, String description, Set<Program> programs, int students) {
        return projectRepository.save(new Project(title, description, programs, students));
    }

    private Project saveWithStatus(String title, ProjectStatus status) {
        Project project = new Project(title, "Not visible to students", Set.of(), 3);
        project.setStatus(status);
        return projectRepository.save(project);
    }

    // ====== Browse Projects ======

    @Test
    void browsePageShowsActiveProjectsOnly() throws Exception {
        save("Autonomous Drone Navigation", "Path planning", Set.of(), 5);
        saveWithStatus("Legacy Course Scheduler", ProjectStatus.ARCHIVED);
        saveWithStatus("Removed Topic", ProjectStatus.DELETED);

        mockMvc.perform(get("/student/projects").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(view().name("student/projects"))
                .andExpect(model().attribute("projects", hasSize(1)))
                .andExpect(content().string(containsString("Autonomous Drone Navigation")))
                .andExpect(content().string(not(containsString("Legacy Course Scheduler"))))
                .andExpect(content().string(not(containsString("Removed Topic"))));
    }

    @Test
    void browsePageListsProjectsAlphabetically() throws Exception {
        Project zebra = save("Zebra Tracker", "Wildlife", Set.of(), 2);
        Project apple = save("Apple Orchard Sensors", "Agriculture", Set.of(), 3);

        mockMvc.perform(get("/student/projects").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projects", contains(apple, zebra)));
    }

    @Test
    void browsePageShowsProjectDetails() throws Exception {
        save("Accessible Transit Mobile App", "Screen-reader support",
                Set.of(Program.SOFTWARE_ENGINEERING), 4);
        save("Autonomous Drone Navigation", "Path planning", Set.of(), 5);

        mockMvc.perform(get("/student/projects").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Screen-reader support")))
                .andExpect(content().string(containsString("Team of 4")))
                .andExpect(content().string(containsString("Software Engineering")))
                .andExpect(content().string(containsString("All programs"))); // open-to-all tag
    }

    @Test
    void browsePageFiltersByProgram() throws Exception {
        save("Accessible Transit Mobile App", "Screen-reader support",
                Set.of(Program.SOFTWARE_ENGINEERING), 4);
        save("Low-Power Wearable Health Monitor", "Heart rate",
                Set.of(Program.ELECTRICAL_ENGINEERING), 3);
        save("Autonomous Drone Navigation", "Path planning", Set.of(), 5);

        mockMvc.perform(get("/student/projects").param("program", "SOFTWARE_ENGINEERING")
                .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedProgram", "SOFTWARE_ENGINEERING"))
                .andExpect(model().attribute("projects", hasSize(2)))
                .andExpect(content().string(containsString("Accessible Transit Mobile App")))
                .andExpect(content().string(containsString("Autonomous Drone Navigation")))
                .andExpect(content().string(not(containsString("Low-Power Wearable Health Monitor"))));
    }

    @Test
    void browsePageSearchesByKeywordInTitleOrDescription() throws Exception {
        save("Autonomous Drone Navigation", "Path planning", Set.of(), 5);
        save("Smart Parking", "Uses a DRONE camera to count cars", Set.of(), 4);
        save("Health Monitor", "Heart rate", Set.of(), 3);

        mockMvc.perform(get("/student/projects").param("keyword", "drone")
                .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("keyword", "drone"))
                .andExpect(model().attribute("projects", hasSize(2)))
                .andExpect(content().string(not(containsString("Health Monitor"))));
    }

    @Test
    void browsePageCombinesKeywordAndProgramFilters() throws Exception {
        save("Drone Firmware", "Embedded", Set.of(Program.ELECTRICAL_ENGINEERING), 3);
        save("Drone Web Portal", "Dashboard", Set.of(Program.SOFTWARE_ENGINEERING), 3);

        mockMvc.perform(get("/student/projects").param("keyword", "drone")
                .param("program", "SOFTWARE_ENGINEERING")
                .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projects", hasSize(1)))
                .andExpect(content().string(containsString("Drone Web Portal")))
                .andExpect(content().string(not(containsString("Drone Firmware"))));
    }

    @Test
    void browsePageWithNoMatchesShowsEmptyMessage() throws Exception {
        save("Autonomous Drone Navigation", "Path planning", Set.of(), 5);

        mockMvc.perform(get("/student/projects").param("keyword", "blockchain")
                .with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projects", empty()))
                .andExpect(content().string(containsString("No project topics found.")));
    }

    @Test
    void browsePageOffersEveryProgramInFilterDropdown() throws Exception {
        mockMvc.perform(get("/student/projects").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("programs", Program.values()))
                .andExpect(model().attribute("keyword", nullValue()))
                .andExpect(model().attribute("selectedProgram", nullValue()))
                .andExpect(content().string(containsString("Biomedical and Electrical Engineering")));
    }

    // ====== Dashboard ======

    @Test
    void dashboardShowsStatsForActiveProjects() throws Exception {
        save("Drone", "A", Set.of(Program.SOFTWARE_ENGINEERING, Program.ELECTRICAL_ENGINEERING), 4);
        save("Parking", "B", Set.of(Program.SOFTWARE_ENGINEERING), 3);
        save("Open Topic", "C", Set.of(), 2);
        saveWithStatus("Archived Topic", ProjectStatus.ARCHIVED); // not counted

        mockMvc.perform(get("/student/dashboard").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"))
                .andExpect(model().attribute("totalProjects", 3))
                .andExpect(model().attribute("totalPrograms", 2))
                .andExpect(model().attribute("totalSpots", 9))
                .andExpect(content().string(containsString("Welcome")));
    }

    @Test
    void dashboardFeaturesAtMostThreeProjects() throws Exception {
        Project a = save("A Project", "a", Set.of(), 1);
        Project b = save("B Project", "b", Set.of(), 1);
        Project c = save("C Project", "c", Set.of(), 1);
        save("D Project", "d", Set.of(), 1);

        mockMvc.perform(get("/student/dashboard").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("featuredProjects", contains(a, b, c)))
                .andExpect(content().string(not(containsString("D Project"))));
    }

    @Test
    void dashboardWithNoProjectsShowsZeroCountsAndEmptyMessage() throws Exception {
        mockMvc.perform(get("/student/dashboard").with(user("student").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("totalProjects", 0))
                .andExpect(model().attribute("totalPrograms", 0))
                .andExpect(model().attribute("totalSpots", 0))
                .andExpect(model().attribute("featuredProjects", List.of()))
                .andExpect(content().string(containsString("No project topics yet.")));
    }

    // ====== Security ======

    @Test
    void professorCannotOpenStudentPages() throws Exception {
        mockMvc.perform(get("/student/dashboard").with(user("prof").roles("PROFESSOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/student/projects").with(user("prof").roles("PROFESSOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void coordinatorCannotOpenStudentPages() throws Exception {
        mockMvc.perform(get("/student/projects").with(user("coord").roles("COORDINATOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/student/projects").accept(MediaType.TEXT_HTML))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }
}
