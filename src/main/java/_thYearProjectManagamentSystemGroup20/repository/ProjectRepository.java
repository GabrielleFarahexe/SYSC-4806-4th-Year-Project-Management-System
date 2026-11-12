package _thYearProjectManagamentSystemGroup20.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import _thYearProjectManagamentSystemGroup20.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.model.Project;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    /** Total projects, ignoring deleted ones. */
    long countByStatusNot(ProjectStatus status);

    long countByStatus(ProjectStatus status);

    /** Newest projects first, for the dashboard's Recent Activity feed. */
    List<Project> findTop5ByStatusNotOrderByCreatedAtDesc(ProjectStatus status);
}