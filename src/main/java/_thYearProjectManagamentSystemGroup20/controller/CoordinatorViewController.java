package _thYearProjectManagamentSystemGroup20.controller;

import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String index(Model model) {
        long totalStudents = studentRepository.count();
        long unassignedCount = studentRepository.countByAssigned(false);
        List<Student> unassignedStudents = studentRepository.findByAssigned(false);

        model.addAttribute("availableProjectCount", projectRepository.countByStatus(ProjectStatus.ACTIVE));
        model.addAttribute("presentationsScheduled", projectRepository.countByScheduledSlotIsNotNull());
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("unassignedCount", unassignedCount);
        model.addAttribute("unassignedStudents", unassignedStudents);

        return "coordinator/dashboard";
    }

    @GetMapping("/coordinator/students")
    public String students(Model model) {
        List<Student> students = StreamSupport.stream(studentRepository.findAll().spliterator(), false)
                .toList();
        long assignedCount = students.stream().filter(Student::isAssigned).count();

        model.addAttribute("students", students);
        model.addAttribute("assignedCount", assignedCount);
        model.addAttribute("unassignedCount", students.size() - assignedCount);
        return "coordinator/students";
    }
}
