package _thYearProjectManagamentSystemGroup20.controller;

import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.service.ProjectService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StudentProjectController {

    private final ProjectService projectService;

    public StudentProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/student/dashboard")
    public String viewDashboard(Model model) {

        List<Project> projects = projectService.getActiveProjects();

        int totalSpots = 0;
        for (Project project : projects) {
            totalSpots += project.getRequiredStudents();
        }

        model.addAttribute("totalProjects", projects.size());
        model.addAttribute("totalPrograms", projectService.getProgramsInUse().size());
        model.addAttribute("totalSpots", totalSpots);
        model.addAttribute("featuredProjects", projects.subList(0, Math.min(3, projects.size())));

        return "student/dashboard";
    }

    @GetMapping("/student/projects")
    public String viewProjects(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String program,
            Model model) {

        model.addAttribute("projects", projectService.searchProjects(keyword, program));
        model.addAttribute("programs", Program.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedProgram", program);

        return "student/projects";
    }
}