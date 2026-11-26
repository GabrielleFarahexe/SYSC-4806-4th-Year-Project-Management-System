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

                        Project parking = new Project("Smart Campus Parking Tracker",
                                        "IoT sensors and a web dashboard showing live parking availability on campus.",
                                        Set.of(Program.COMPUTER_SYSTEMS_ENGINEERING, Program.ELECTRICAL_ENGINEERING),
                                        4);
                        parking.assignTeam("admin", Set.of("admin"));
                        repository.save(parking);
                        Project review = new Project("ML-Based Code Review Assistant",
                                        "A model that flags common bugs in student pull requests.",
                                        Set.of(Program.SOFTWARE_ENGINEERING, Program.COMPUTER_SYSTEMS_ENGINEERING), 3);
                        review.assignTeam("professor", Set.of());
                        repository.save(review);
                        Project drone = new Project("Autonomous Drone Navigation",
                                        "Obstacle avoidance and path planning for an indoor quadcopter.",
                                        Set.of(), 5);
                        drone.assignTeam("professor", Set.of());
                        repository.save(drone); // empty = open to all programs
                        Project transit = new Project("Accessible Transit Mobile App",
                                        "An OC Transpo app focused on screen-reader support.",
                                        Set.of(Program.SOFTWARE_ENGINEERING), 4);
                        transit.assignTeam("professor", Set.of());
                        repository.save(transit);
                        Project monitor = new Project("Low-Power Wearable Health Monitor",
                                        "A wrist-worn device that tracks heart rate for a week on one charge.",
                                        Set.of(Program.ELECTRICAL_ENGINEERING), 3);
                        monitor.assignTeam("professor", Set.of());
                        repository.save(monitor);

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
                                        new Student("101200001", "Maya Chen", "maya.chen@example.com", false),
                                        new Student("101200002", "Noah Patel", "noah.patel@example.com", false),
                                        new Student("101200003", "Sofia Martin", "sofia.martin@example.com", false)));
                };
        }
}
