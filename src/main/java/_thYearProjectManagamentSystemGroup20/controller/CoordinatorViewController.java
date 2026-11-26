package _thYearProjectManagamentSystemGroup20.controller;

import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.StreamSupport;

@Controller
public class CoordinatorViewController {
    private final StudentRepository studentRepository;
    private final ProjectRepository projectRepository;

    public CoordinatorViewController(StudentRepository studentRepository, ProjectRepository projectRepository) {
        this.studentRepository = studentRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/coordinator/dashboard")
    public String index(Model model, Principal principal) {
        long totalStudents = studentRepository.count();
        long unassignedCount = studentRepository.countByAssigned(false);
        List<Student> unassignedStudents = studentRepository.findByAssigned(false);
        long availableProjectCount = projectRepository.countByStatus(ProjectStatus.ACTIVE);

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("unassignedCount", unassignedCount);
        model.addAttribute("unassignedStudents", unassignedStudents);
        model.addAttribute("availableProjectCount", availableProjectCount);
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        return "coordinator/dashboard";
    }

    @GetMapping("/coordinator/students")
    public String students(Model model, Principal principal) {
        List<Student> students = StreamSupport.stream(studentRepository.findAll().spliterator(), false)
                .toList();
        long assignedCount = students.stream().filter(Student::isAssigned).count();

        model.addAttribute("students", students);
        model.addAttribute("assignedCount", assignedCount);
        model.addAttribute("unassignedCount", students.size() - assignedCount);
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        return "coordinator/students";
    }

    @GetMapping("/coordinator/students/unassigned")
    public String unassignedStudents(Model model, Principal principal) {
        model.addAttribute("unassignedStudents", studentRepository.findByAssigned(false));
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
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