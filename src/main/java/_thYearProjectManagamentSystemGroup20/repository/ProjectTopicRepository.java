package _thYearProjectManagamentSystemGroup20.repository;

import _thYearProjectManagamentSystemGroup20.model.ProjectTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import _thYearProjectManagamentSystemGroup20.model.ProjectTopicStatus;
import java.util.List;

public interface ProjectTopicRepository extends JpaRepository<ProjectTopic, Long> {
    List<ProjectTopic> findByStatusOrderByTitleAsc(ProjectTopicStatus status);
}