package _thYearProjectManagamentSystemGroup20.repository;

import _thYearProjectManagamentSystemGroup20.model.Project;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    java.util.Optional<Project> findByMemberUsernamesContaining(String username);
    List<Project> findByProfessorUsernameOrderByIdAsc(String username);
    java.util.Optional<Project> findByIdAndProfessorUsername(Long id, String username);

    long countByScheduledSlotIsNotNull();

    @org.springframework.data.jpa.repository.Query("select p.scheduledSlot from Project p where p.professorUsername = :username and p.scheduledSlot is not null")
    List<String> reservedSlots(String username);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Project p where p.id = :id")
    java.util.Optional<Project> lockProject(Long id);

    /** Total projects, ignoring deleted ones. */
    long countByStatusNot(ProjectStatus status);

    long countByStatus(ProjectStatus status);

    /** Newest projects first, for the dashboard's Recent Activity feed. */
    List<Project> findTop5ByStatusNotOrderByCreatedAtDesc(ProjectStatus status);

    /** Active projects in alphabetical order, for the student pages. */
    List<Project> findByStatusOrderByTitleAsc(ProjectStatus status);
}