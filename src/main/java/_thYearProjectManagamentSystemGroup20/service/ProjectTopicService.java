package _thYearProjectManagamentSystemGroup20.service;

import _thYearProjectManagamentSystemGroup20.model.ProjectTopic;
import _thYearProjectManagamentSystemGroup20.model.ProjectTopicStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectTopicRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    public List<ProjectTopic> getActiveProjectTopics() {
        return projectTopicRepository.findByStatusOrderByTitleAsc(ProjectTopicStatus.ACTIVE);
    }

    public List<ProjectTopic> searchProjectTopics(String keyword, String program) {
        List<ProjectTopic> results = new ArrayList<>();

        for (ProjectTopic topic : getActiveProjectTopics()) {
            if (matchesKeyword(topic, keyword) && matchesProgram(topic, program)) {
                results.add(topic);
            }
        }

        return results;
    }

    public Set<String> getAllPrograms() {
        Set<String> programs = new TreeSet<>();

        for (ProjectTopic topic : getActiveProjectTopics()) {
            for (String program : topic.getProgramRestrictions().split(",")) {
                program = program.trim();
                if (!program.equalsIgnoreCase("All programs")) {
                    programs.add(program);
                }
            }
        }

        return programs;
    }

    private boolean matchesKeyword(ProjectTopic topic, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        String k = keyword.toLowerCase();
        return topic.getTitle().toLowerCase().contains(k)
                || topic.getDescription().toLowerCase().contains(k);
    }

    private boolean matchesProgram(ProjectTopic topic, String program) {
        if (program == null || program.isBlank()) {
            return true;
        }
        String restrictions = topic.getProgramRestrictions().toLowerCase();
        return restrictions.contains("all programs") || restrictions.contains(program.toLowerCase());
    }
}