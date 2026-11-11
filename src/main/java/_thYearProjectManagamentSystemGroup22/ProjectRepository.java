package _thYearProjectManagamentSystemGroup22;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    /** Total projects, ignoring deleted ones. */
    long countByStatusNot(ProjectStatus status);

    long countByStatus(ProjectStatus status);

    /** Newest projects first, for the dashboard's Recent Activity feed. */
    List<Project> findTop5ByStatusNotOrderByCreatedAtDesc(ProjectStatus status);
}