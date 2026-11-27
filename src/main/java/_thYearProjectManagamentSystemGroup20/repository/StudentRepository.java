package _thYearProjectManagamentSystemGroup20.repository;

import _thYearProjectManagamentSystemGroup20.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "students")
public interface StudentRepository extends CrudRepository<Student, Long> {
    List<Student> findByAssigned(boolean assigned);

    long countByAssigned(boolean assigned);

    Optional<Student> findByStudentNumber(String studentNumber);
}