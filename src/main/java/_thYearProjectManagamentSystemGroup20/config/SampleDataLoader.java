package _thYearProjectManagamentSystemGroup20.config;

import _thYearProjectManagamentSystemGroup20.model.ProjectTopic;
import _thYearProjectManagamentSystemGroup20.model.ProjectTopicStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectTopicRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataLoader {

    @Bean
    CommandLineRunner loadSampleData(ProjectTopicRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            repository.save(new ProjectTopic("Smart Campus Parking Tracker",
                    "IoT sensors and a web dashboard showing live parking availability on campus.",
                    "Computer Systems Engineering, Electrical Engineering", 4));
            repository.save(new ProjectTopic("ML-Based Code Review Assistant",
                    "A model that flags common bugs in student pull requests.",
                    "Software Engineering, Computer Systems Engineering", 3));
            repository.save(new ProjectTopic("Autonomous Drone Navigation",
                    "Obstacle avoidance and path planning for an indoor quadcopter.",
                    "All programs", 5));
            repository.save(new ProjectTopic("Accessible Transit Mobile App",
                    "An OC Transpo app focused on screen-reader support.",
                    "Software Engineering", 4));
            repository.save(new ProjectTopic("Low-Power Wearable Health Monitor",
                    "A wrist-worn device that tracks heart rate for a week on one charge.",
                    "Electrical Engineering", 3));

            ProjectTopic archived = new ProjectTopic("Legacy Course Scheduler",
                    "Old topic that students should not see.", "All programs", 3);
            archived.setStatus(ProjectTopicStatus.ARCHIVED);
            repository.save(archived);
        };
    }
}