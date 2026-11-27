package _thYearProjectManagamentSystemGroup20.service;

import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher events;

    public ProjectService(ProjectRepository projectRepository,
            ApplicationEventPublisher events) {
        this.projectRepository = projectRepository;
        this.events = events;
    }

    // Publishes an event instead of updating the student directly,
    // so this module never depends on the students module.
    @Transactional
    public void assignStudent(String studentNumber, Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("No project with id " + projectId);
        }
        events.publishEvent(new StudentAssignedToProject(studentNumber, projectId));
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findByStatusOrderByTitleAsc(ProjectStatus.ACTIVE);
    }

    public List<Project> searchProjects(String keyword, String program) {
        List<Project> results = new ArrayList<>();

        for (Project project : getActiveProjects()) {
            if (matchesKeyword(project, keyword) && matchesProgram(project, program)) {
                results.add(project);
            }
        }

        return results;
    }

    // Programs used by active projects, for the dashboard count
    public Set<Program> getProgramsInUse() {
        Set<Program> programs = new TreeSet<>();
        for (Project project : getActiveProjects()) {
            programs.addAll(project.getProgramRestrictions());
        }
        return programs;
    }

    private boolean matchesKeyword(Project project, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String k = keyword.toLowerCase();
        return project.getTitle().toLowerCase().contains(k)
                || project.getDescription().toLowerCase().contains(k);
    }

    // program is the enum name from the dropdown, e.g. "SOFTWARE_ENGINEERING"
    private boolean matchesProgram(Project project, String program) {
        if (program == null || program.isBlank()) {
            return true;
        }
        if (project.isOpenToAllPrograms()) {
            return true;
        }
        for (Program p : project.getProgramRestrictions()) {
            if (p.name().equals(program)) {
                return true;
            }
        }
        return false;
    }
}