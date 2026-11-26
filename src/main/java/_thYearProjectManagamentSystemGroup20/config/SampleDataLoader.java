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

import java.util.List;
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

        @Bean
        CommandLineRunner loadSampleStudents(StudentRepository repository) {
                return args -> {
                        if (repository.count() > 0) {
                                return;
                        }

                        repository.saveAll(List.of(
                                        new Student("101200001", "Maya Chen", "maya.chen@example.com", "Software Engineering", false),
                                        new Student("101200002", "Noah Patel", "noah.patel@example.com", "Computer Systems", false),
                                        new Student("101200003", "Sofia Martin", "sofia.martin@example.com", "Electrical Engineering",false)));
                };
        }
}
