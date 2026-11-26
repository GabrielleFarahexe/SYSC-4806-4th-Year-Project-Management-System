package _thYearProjectManagamentSystemGroup20;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import _thYearProjectManagamentSystemGroup20.Availability.AvailabilityService;

import java.util.List;
import _thYearProjectManagamentSystemGroup20.Availability.Weekday;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@Transactional
class AvailabilityTest {

	@Autowired
	private AvailabilityService availabilityService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;
    private Long projectId;

	@Autowired
    private _thYearProjectManagamentSystemGroup20.repository.ProjectRepository projects;

    @BeforeEach
	void setUp() {
		var project = new _thYearProjectManagamentSystemGroup20.model.Project("Team", "Test project", java.util.Set.of(), 2);
        project.assignTeam("professor", java.util.Set.of("student", "teammate"));
        projectId = projects.save(project).getId();
        availabilityService.saveAvailability("professor", projectId, List.of("MONDAY_9", "FRIDAY_15", "THURSDAY_14"));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity()).build();
	}

	@Test
	void userWithoutRoleCannotAccessAvailability() throws Exception {
		mockMvc.perform(get("/availability").with(user("user")))
				.andExpect(status().isForbidden());
	}

	@Test
	void anonymousUserIsRedirectedToLogin() throws Exception {
		mockMvc.perform(get("/availability").accept(org.springframework.http.MediaType.TEXT_HTML))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void availabilityPageShowsSavedSlotsAndSchedule() throws Exception {
		availabilityService.saveTeamSlots("student", List.of("MONDAY_9", "FRIDAY_15"));

		mockMvc.perform(get("/availability").with(user("student").roles("STUDENT")))
				.andExpect(status().isOk())
				.andExpect(view().name("availability"))
				.andExpect(model().attribute("days", List.of(Weekday.MONDAY, Weekday.TUESDAY,
						Weekday.WEDNESDAY, Weekday.THURSDAY, Weekday.FRIDAY)))
				.andExpect(model().attribute("hours", List.of(9, 10, 11, 12, 13, 14, 15)))
				.andExpect(model().attribute("selectedSlots",
						org.hamcrest.Matchers.containsInAnyOrder("MONDAY_9", "FRIDAY_15")));
	}

	@Test
	void submittingAvailabilitySavesSlotsForAuthenticatedUser() throws Exception {
		mockMvc.perform(post("/availability").with(user("student").roles("STUDENT"))
				.with(csrf()).param("slots", "MONDAY_9", "THURSDAY_14"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/availability"))
				.andExpect(flash().attribute("success", "Availability saved successfully!"));

		assertThat(availabilityService.teamSlots("teammate"))
				.containsExactlyInAnyOrder("MONDAY_9", "THURSDAY_14");
	}

    @Test
    void coordinatorSchedulesCommonTimeAndReservesItAcrossProfessorProjects() throws Exception {
        var other = new _thYearProjectManagamentSystemGroup20.model.Project("Other team", "Same professor", java.util.Set.of(), 1);
        other.assignTeam("professor", java.util.Set.of("other-member"));
        projects.save(other);
        availabilityService.saveAvailability("professor", other.getId(), List.of("MONDAY_9"));
        availabilityService.saveTeamSlots("student", List.of("MONDAY_9"));
        availabilityService.saveTeamSlots("other-member", List.of("MONDAY_9"));
        var coordinator = user("coordinator").roles("COORDINATOR");
        String page = "/coordinator/presentations";
        mockMvc.perform(get(page).with(coordinator)).andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("View availability")));
        mockMvc.perform(post(page + "/" + projectId).with(coordinator).with(csrf()).param("slot", "FRIDAY_15"))
                .andExpect(flash().attributeExists("error"));
        mockMvc.perform(post(page + "/" + projectId).with(coordinator).with(csrf()).param("slot", "MONDAY_9"))
                .andExpect(redirectedUrl(page));
        mockMvc.perform(get(page).with(coordinator)).andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Done · Monday 9:00")));
        assertThat(availabilityService.allowedSlots("other-member")).isEmpty();
        assertThatThrownBy(() -> availabilityService.schedule(other.getId(), "MONDAY_9"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> availabilityService.saveAvailability("professor", other.getId(), List.of("MONDAY_9")))
                .isInstanceOf(IllegalArgumentException.class);
        availabilityService.saveTeamSlots("student", List.of("FRIDAY_15"));
        mockMvc.perform(get(page).with(coordinator))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Change availability")));
        mockMvc.perform(post(page + "/" + projectId).with(coordinator).with(csrf()).param("slot", "FRIDAY_15"))
                .andExpect(redirectedUrl(page));
        assertThat(availabilityService.reservedSlots("professor")).containsExactly("FRIDAY_15");
        assertThat(availabilityService.allowedSlots("other-member")).containsExactly("MONDAY_9");
    }

    @Test
    void projectSelectionMustStayWithinProfessorHours() {
        availabilityService.saveTeamSlots("student", List.of("MONDAY_9", "FRIDAY_15"));
        assertThatThrownBy(() -> availabilityService.saveTeamSlots("teammate", List.of("MONDAY_10")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> availabilityService.saveTeamSlots("unassigned", List.of("MONDAY_9")))
                .isInstanceOf(IllegalArgumentException.class);
        availabilityService.saveAvailability("professor", projectId, List.of("FRIDAY_15"));
        assertThat(availabilityService.teamSlots("teammate")).containsExactly("FRIDAY_15");
    }

	@Test
	void submittingNoSlotsClearsAvailability() throws Exception {
		availabilityService.saveAvailability("professor", projectId, List.of("FRIDAY_15"));

		mockMvc.perform(post("/availability").with(user("professor").roles("PROFESSOR")).param("projectId", projectId.toString())
				.with(csrf()))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/availability?projectId=" + projectId));

		assertThat(availabilityService.getAvailability("professor", projectId)).isEmpty();
	}

	@Test
	void submittingInvalidSlotReturnsBadRequestAndKeepsExistingSlots() throws Exception {
		availabilityService.saveTeamSlots("student", List.of("MONDAY_9"));

		mockMvc.perform(post("/availability").with(user("student").roles("STUDENT"))
				.with(csrf()).param("slots", "MONDAY_9", "SATURDAY_9"))
				.andExpect(status().isBadRequest());

		assertThat(availabilityService.teamSlots("student")).containsExactly("MONDAY_9");
	}

	@Test
	void submittingWithoutCsrfTokenDoesNotChangeAvailability() throws Exception {
		availabilityService.saveTeamSlots("student", List.of("MONDAY_9"));

		mockMvc.perform(post("/availability").with(user("student").roles("STUDENT"))
				.param("slots", "MONDAY_9"))
				.andExpect(status().isForbidden());

		assertThat(availabilityService.teamSlots("student")).containsExactly("MONDAY_9");
	}

	@Test
	void updatingAnotherProjectOfTheSameProfessorPreservesTeamAvailability() {
        var second = new _thYearProjectManagamentSystemGroup20.model.Project("Second", "Other team", java.util.Set.of(), 1);
        second.assignTeam("professor", java.util.Set.of());
        projects.save(second);
        availabilityService.saveAvailability("professor", second.getId(), List.of("TUESDAY_10"));
        assertThat(availabilityService.allowedSlots("student")).containsExactlyInAnyOrder("MONDAY_9", "FRIDAY_15", "THURSDAY_14");
        assertThat(availabilityService.getAvailability("professor", second.getId())).containsExactly("TUESDAY_10");
    }

	@Test
	void invalidSlotsLeaveExistingAvailabilityIntact() {
		availabilityService.saveAvailability("professor", projectId, List.of("WEDNESDAY_12"));
		for (String invalidSlot : List.of("SATURDAY_9", "MONDAY_16", "invalid")) {
			assertThatThrownBy(() -> availabilityService.saveAvailability("professor", projectId,
					List.of("MONDAY_9", invalidSlot))).isInstanceOf(IllegalArgumentException.class);
			assertThat(availabilityService.getAvailability("professor", projectId))
					.containsExactly("WEDNESDAY_12");
		}
	}

}
