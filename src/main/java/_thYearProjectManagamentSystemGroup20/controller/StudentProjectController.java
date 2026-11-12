package _thYearProjectManagamentSystemGroup20.controller;

import _thYearProjectManagamentSystemGroup20.model.ProjectTopic;
import _thYearProjectManagamentSystemGroup20.service.ProjectTopicService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StudentProjectController {

    private final ProjectTopicService projectTopicService;

    public StudentProjectController(ProjectTopicService projectTopicService) {
        this.projectTopicService = projectTopicService;
    }

    @GetMapping("/student/dashboard")
    public String viewDashboard(Model model) {

        List<ProjectTopic> projects = projectTopicService.getActiveProjectTopics();

        int totalSpots = 0;
        for (ProjectTopic project : projects) {
            totalSpots += project.getRequiredStudents();
        }

        model.addAttribute("totalProjects", projects.size());
        model.addAttribute("totalPrograms", projectTopicService.getAllPrograms().size());
        model.addAttribute("totalSpots", totalSpots);
        model.addAttribute("featuredProjects", projects.subList(0, Math.min(3, projects.size())));

        return "student/dashboard";
    }

    @GetMapping("/student/projects")
    public String viewProjects(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String program,
            Model model) {

        model.addAttribute("projects", projectTopicService.searchProjectTopics(keyword, program));
        model.addAttribute("programs", projectTopicService.getAllPrograms());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedProgram", program);

        return "student/projects";
    }
}