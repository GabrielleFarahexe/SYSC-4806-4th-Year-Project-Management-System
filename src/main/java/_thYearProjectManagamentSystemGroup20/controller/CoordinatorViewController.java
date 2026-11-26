package _thYearProjectManagamentSystemGroup20.controller;

import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CoordinatorViewController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/coordinator/dashboard")
    public String index(Model model) {
        long totalStudents = studentRepository.count();
        long unassignedCount = studentRepository.countByAssigned(false);
        List<Student> unassignedStudents = studentRepository.findByAssigned(false);
        long totalProjects = projectRepository.countByStatusNot(ProjectStatus.DELETED);

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("unassignedCount", unassignedCount);
        model.addAttribute("unassignedStudents", unassignedStudents);
        model.addAttribute("totalProjects", totalProjects);

        return "coordinator/dashboard";
    }

    @GetMapping("/coordinator/students/unassigned")
    public String unassignedStudents(Model model) {
        model.addAttribute("unassignedStudents", studentRepository.findByAssigned(false));
        return "coordinator/unassigned-students";
    }

    @PostMapping("/students/{id}/remind")
    public String sendReminder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentRepository.findById(id).ifPresent(student ->
                redirectAttributes.addFlashAttribute("reminderMessage",
                        "Reminder sent to " + student.getName() + " (" + student.getEmail() + ")")
        );
        return "redirect:/coordinator/students/unassigned";
    }
}
