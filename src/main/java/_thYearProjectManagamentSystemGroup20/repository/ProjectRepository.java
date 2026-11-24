package _thYearProjectManagamentSystemGroup20.repository;

import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    /** Total projects, ignoring deleted ones. */
    long countByStatusNot(ProjectStatus status);

    long countByStatus(ProjectStatus status);

    /** Newest projects first, for the dashboard's Recent Activity feed. */
    List<Project> findTop5ByStatusNotOrderByCreatedAtDesc(ProjectStatus status);

    /** Active projects in alphabetical order, for the student pages. */
    List<Project> findByStatusOrderByTitleAsc(ProjectStatus status);
}