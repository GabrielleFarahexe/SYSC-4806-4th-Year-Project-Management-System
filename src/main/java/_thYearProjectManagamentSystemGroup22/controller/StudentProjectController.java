package _thYearProjectManagamentSystemGroup22.controller;

import _thYearProjectManagamentSystemGroup22.model.ProjectTopic;
import _thYearProjectManagamentSystemGroup22.service.ProjectTopicService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StudentProjectController {

    private final ProjectTopicService projectTopicService;

    public StudentProjectController(ProjectTopicService projectTopicService) {
        this.projectTopicService = projectTopicService;
    }

    @GetMapping("/student/projects")
    public String viewProjects(Model model) {

        List<ProjectTopic> projects = projectTopicService.getAllProjectTopics();

        model.addAttribute("projects", projects);

        return "student/projects";
    }
}