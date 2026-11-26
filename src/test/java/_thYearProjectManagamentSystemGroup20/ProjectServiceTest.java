package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import _thYearProjectManagamentSystemGroup20.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests: the repository is a Mockito mock, so no Spring or database is
 * needed.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @InjectMocks
    private ProjectService service;

    private Project drone;
    private Project transit;

    @BeforeEach
    void setUp() {
        drone = new Project("Autonomous Drone Navigation", "Path planning for a quadcopter", Set.of(), 5);
        transit = new Project("Accessible Transit App", "Screen-reader support",
                Set.of(Program.SOFTWARE_ENGINEERING), 4);
        when(repository.findByStatusOrderByTitleAsc(ProjectStatus.ACTIVE))
                .thenReturn(List.of(transit, drone));
    }

    @Test
    void searchByKeywordIgnoresCase() {
        assertEquals(List.of(drone), service.searchProjects("DRONE", null));
    }

    @Test
    void filterByProgramIncludesOpenToAllProjects() {
        assertEquals(List.of(drone), service.searchProjects(null, "ELECTRICAL_ENGINEERING"));
    }

    @Test
    void filterByProgramIncludesMatchingRestriction() {
        assertEquals(List.of(transit, drone), service.searchProjects(null, "SOFTWARE_ENGINEERING"));
    }

    @Test
    void blankSearchReturnsAllActiveProjects() {
        assertEquals(2, service.searchProjects("", "").size());
    }

    @Test
    void programsInUseOnlyCountsRestrictedPrograms() {
        assertEquals(Set.of(Program.SOFTWARE_ENGINEERING), service.getProgramsInUse());
    }
}