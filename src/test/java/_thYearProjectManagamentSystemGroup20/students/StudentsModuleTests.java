package _thYearProjectManagamentSystemGroup20.students;

import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import _thYearProjectManagamentSystemGroup20.service.StudentAssignedToProject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;
import org.springframework.modulith.test.Scenario;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest(mode = BootstrapMode.DIRECT_DEPENDENCIES, extraIncludes = "model")
class StudentsModuleTests {

    @Autowired
    StudentRepository students;

    @Test
    void marksStudentAssigned(Scenario scenario) {
        students.save(new Student("999", "Test Student", "test@cmail.carleton.ca", false));

        scenario.publish(new StudentAssignedToProject("999", 1L))
                .andWaitForStateChange(() -> students.findByStudentNumber("999")
                        .map(Student::isAssigned)
                        .filter(assigned -> assigned)
                        .orElse(null))
                .andVerify(assigned -> assertThat(assigned).isTrue());
    }
}