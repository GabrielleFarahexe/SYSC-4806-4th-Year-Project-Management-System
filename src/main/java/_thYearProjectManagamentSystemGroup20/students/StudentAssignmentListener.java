package _thYearProjectManagamentSystemGroup20.students;

import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import _thYearProjectManagamentSystemGroup20.service.StudentAssignedToProject;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
class StudentAssignmentListener {

    private final StudentRepository students;

    StudentAssignmentListener(StudentRepository students) {
        this.students = students;
    }

    @ApplicationModuleListener
    void on(StudentAssignedToProject event) {
        students.findByStudentNumber(event.studentNumber())
                .ifPresent(student -> {
                    student.setAssigned(true);
                    students.save(student);
                });
    }
}