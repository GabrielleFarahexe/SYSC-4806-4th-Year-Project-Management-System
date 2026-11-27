package _thYearProjectManagamentSystemGroup20.controller;

import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import _thYearProjectManagamentSystemGroup20.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CoordinatorViewController {

    private final StudentRepository studentRepository;
    private final ProjectService projectService;

    public CoordinatorViewController(StudentRepository studentRepository,
            ProjectService projectService) {
        this.studentRepository = studentRepository;
        this.projectService = projectService;
    }

    @GetMapping("/coordinator/dashboard")
    public String index(Model model) {
        long totalStudents = studentRepository.count();
        long unassignedCount = studentRepository.countByAssigned(false);
        List<Student> unassignedStudents = studentRepository.findByAssigned(false);

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("unassignedCount", unassignedCount);
        model.addAttribute("unassignedStudents", unassignedStudents);
        model.addAttribute("projects", projectService.getActiveProjects());

        return "coordinator/dashboard";
    }

    @PostMapping("/coordinator/assign")
    public String assign(@RequestParam String studentNumber,
            @RequestParam Long projectId) {
        projectService.assignStudent(studentNumber, projectId);
        return "redirect:/coordinator/dashboard";
    }
}
