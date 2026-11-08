package _thYearProjectManagamentSystemGroup22.controller;

import _thYearProjectManagamentSystemGroup22.model.Student;
import _thYearProjectManagamentSystemGroup22.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CoordinatorViewController {
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/coordinator/dashboard")
    public String index(Model model) {
        long totalStudents = studentRepository.count();
        long unassignedCount = studentRepository.countByAssigned(false);
        List<Student> unassignedStudents = studentRepository.findByAssigned(false);

        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("unassignedCount", unassignedCount);
        model.addAttribute("unassignedStudents", unassignedStudents);

        return "coordinator/dashboard";
    }
}
