package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.model.ProjectTopic;
import _thYearProjectManagamentSystemGroup20.model.ProjectTopicStatus;
import _thYearProjectManagamentSystemGroup20.service.ProjectTopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectTopicServiceTest {

    @Autowired
    private ProjectTopicService projectTopicService;

    @Test
    void onlyActiveTopicsAreReturned() {
        for (ProjectTopic topic : projectTopicService.getActiveProjectTopics()) {
            assertEquals(ProjectTopicStatus.ACTIVE, topic.getStatus());
        }
    }

    @Test
    void searchByKeyword() {
        List<ProjectTopic> results = projectTopicService.searchProjectTopics("drone", null);

        assertEquals(1, results.size());
        assertEquals("Autonomous Drone Navigation", results.get(0).getTitle());
    }

    @Test
    void searchByProgramIncludesAllProgramsTopics() {
        List<ProjectTopic> results = projectTopicService.searchProjectTopics(null, "Software Engineering");

        for (ProjectTopic topic : results) {
            String restrictions = topic.getProgramRestrictions();
            assertTrue(restrictions.contains("Software Engineering") || restrictions.equals("All programs"));
        }
    }
}