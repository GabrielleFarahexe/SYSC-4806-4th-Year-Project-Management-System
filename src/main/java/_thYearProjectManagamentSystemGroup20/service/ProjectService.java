package _thYearProjectManagamentSystemGroup20.service;

import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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