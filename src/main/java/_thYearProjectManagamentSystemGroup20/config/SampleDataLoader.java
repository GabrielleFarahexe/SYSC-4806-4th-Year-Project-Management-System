package _thYearProjectManagamentSystemGroup20.config;

import _thYearProjectManagamentSystemGroup20.model.Program;
import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class SampleDataLoader {

        @Bean
        CommandLineRunner loadSampleData(ProjectRepository repository) {
                return args -> {
                        if (repository.count() > 0) {
                                return;
                        }

                        repository.save(new Project("Smart Campus Parking Tracker",
                                        "IoT sensors and a web dashboard showing live parking availability on campus.",
                                        Set.of(Program.COMPUTER_SYSTEMS_ENGINEERING, Program.ELECTRICAL_ENGINEERING),
                                        4));
                        repository.save(new Project("ML-Based Code Review Assistant",
                                        "A model that flags common bugs in student pull requests.",
                                        Set.of(Program.SOFTWARE_ENGINEERING, Program.COMPUTER_SYSTEMS_ENGINEERING), 3));
                        repository.save(new Project("Autonomous Drone Navigation",
                                        "Obstacle avoidance and path planning for an indoor quadcopter.",
                                        Set.of(), 5)); // empty = open to all programs
                        repository.save(new Project("Accessible Transit Mobile App",
                                        "An OC Transpo app focused on screen-reader support.",
                                        Set.of(Program.SOFTWARE_ENGINEERING), 4));
                        repository.save(new Project("Low-Power Wearable Health Monitor",
                                        "A wrist-worn device that tracks heart rate for a week on one charge.",
                                        Set.of(Program.ELECTRICAL_ENGINEERING), 3));

                        Project archived = new Project("Legacy Course Scheduler",
                                        "Old topic that students should not see.", Set.of(), 3);
                        archived.setStatus(ProjectStatus.ARCHIVED);
                        repository.save(archived);
                };
        }

        // Unassigned students for the coordinator dashboard demo.
        @Bean
        CommandLineRunner loadSampleStudents(StudentRepository students) {
                return args -> {
                        if (students.count() > 0) {
                                return;
                        }

                        students.save(new Student("101100001", "Alice Martin", "alice@cmail.carleton.ca", false));
                        students.save(new Student("101100002", "Ben Tremblay", "ben@cmail.carleton.ca", false));
                        students.save(new Student("101100003", "Chloe Nguyen", "chloe@cmail.carleton.ca", false));
                        students.save(new Student("101100004", "David Singh", "david@cmail.carleton.ca", false));
                        students.save(new Student("101100005", "Emma Roy", "emma@cmail.carleton.ca", true));
                };
        }
}