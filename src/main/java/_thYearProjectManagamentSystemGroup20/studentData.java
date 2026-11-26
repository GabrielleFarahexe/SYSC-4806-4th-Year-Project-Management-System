package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.model.Student;
import _thYearProjectManagamentSystemGroup20.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class studentData {
    @Bean
    CommandLineRunner seedStudents(StudentRepository studentRepository) {
        return args -> {
            if (studentRepository.count() == 0) {
                studentRepository.save(new Student("101200001", "Alice Smith", "alice@cmail.carleton.ca", "Software Engineering", true));
                studentRepository.save(new Student("101200002", "Bob Jones", "bob@cmail.carleton.ca", "Computer Systems", false));
                studentRepository.save(new Student("101200003", "Charlie Lee", "charlie@cmail.carleton.ca", "Electrical Engineering", false));
                studentRepository.save(new Student("101999999", "Demo Student", "demo.student@cmail.carleton.ca", "Software Engieering", false));
            }
        };
    }
}
