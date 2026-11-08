package _thYearProjectManagamentSystemGroup20.service;

import _thYearProjectManagamentSystemGroup20.model.ProjectTopic;
import _thYearProjectManagamentSystemGroup20.repository.ProjectTopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTopicService {

    private final ProjectTopicRepository projectTopicRepository;

    public ProjectTopicService(ProjectTopicRepository projectTopicRepository) {
        this.projectTopicRepository = projectTopicRepository;
    }

    public ProjectTopic createProjectTopic(ProjectTopic projectTopic) {
        return projectTopicRepository.save(projectTopic);
    }

    public List<ProjectTopic> getAllProjectTopics() {
        return projectTopicRepository.findAll();
    }
}