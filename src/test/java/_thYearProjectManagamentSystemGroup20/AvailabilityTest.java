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
import java.time.DayOfWeek;

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

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(springSecurity()).build();
	}

	@Test
	void defaultUserCanOpenAndSaveAvailabilityWithoutStudentOrProfessorRole() throws Exception {
		mockMvc.perform(get("/availability").with(user("user")))
				.andExpect(status().isOk());

		mockMvc.perform(post("/availability").with(user("user")).with(csrf())
				.param("slots", "MONDAY_9"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/availability"));

		assertThat(availabilityService.getAvailability("user")).containsExactly("MONDAY_9");
	}

	@Test
	void anonymousUserIsRedirectedToLogin() throws Exception {
		mockMvc.perform(get("/availability").accept(org.springframework.http.MediaType.TEXT_HTML))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
	}

	@Test
	void availabilityPageShowsSavedSlotsAndSchedule() throws Exception {
		availabilityService.saveAvailability("student", List.of("MONDAY_9", "FRIDAY_15"));
		availabilityService.saveAvailability("other-student", List.of("TUESDAY_10"));

		mockMvc.perform(get("/availability").with(user("student").roles("STUDENT")))
				.andExpect(status().isOk())
				.andExpect(view().name("availability"))
				.andExpect(model().attribute("days", List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
						DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)))
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

		assertThat(availabilityService.getAvailability("student"))
				.containsExactlyInAnyOrder("MONDAY_9", "THURSDAY_14");
	}

	@Test
	void submittingNoSlotsClearsAvailability() throws Exception {
		availabilityService.saveAvailability("professor", List.of("FRIDAY_15"));

		mockMvc.perform(post("/availability").with(user("professor").roles("PROFESSOR"))
				.with(csrf()))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/availability"));

		assertThat(availabilityService.getAvailability("professor")).isEmpty();
	}

	@Test
	void submittingInvalidSlotReturnsBadRequestAndKeepsExistingSlots() throws Exception {
		availabilityService.saveAvailability("student", List.of("WEDNESDAY_12"));

		mockMvc.perform(post("/availability").with(user("student").roles("STUDENT"))
				.with(csrf()).param("slots", "MONDAY_9", "SATURDAY_9"))
				.andExpect(status().isBadRequest());

		assertThat(availabilityService.getAvailability("student")).containsExactly("WEDNESDAY_12");
	}

	@Test
	void submittingWithoutCsrfTokenDoesNotChangeAvailability() throws Exception {
		availabilityService.saveAvailability("student", List.of("WEDNESDAY_12"));

		mockMvc.perform(post("/availability").with(user("student").roles("STUDENT"))
				.param("slots", "MONDAY_9"))
				.andExpect(status().isForbidden());

		assertThat(availabilityService.getAvailability("student")).containsExactly("WEDNESDAY_12");
	}

	@Test
	void availabilityCanBeReplacedAndClearedWithoutChangingAnotherUser() {
		availabilityService.saveAvailability("student", List.of("MONDAY_9", "MONDAY_10", "MONDAY_9"));
		availabilityService.saveAvailability("professor", List.of("FRIDAY_15"));
		assertThat(availabilityService.getAvailability("student"))
				.containsExactlyInAnyOrder("MONDAY_9", "MONDAY_10");

		availabilityService.saveAvailability("student", List.of("TUESDAY_11"));
		assertThat(availabilityService.getAvailability("student")).containsExactly("TUESDAY_11");
		availabilityService.saveAvailability("student", List.of());
		assertThat(availabilityService.getAvailability("student")).isEmpty();
		assertThat(availabilityService.getAvailability("professor")).containsExactly("FRIDAY_15");
	}

	@Test
	void invalidSlotsLeaveExistingAvailabilityIntact() {
		availabilityService.saveAvailability("validation-user", List.of("WEDNESDAY_12"));
		for (String invalidSlot : List.of("SATURDAY_9", "MONDAY_16", "invalid")) {
			assertThatThrownBy(() -> availabilityService.saveAvailability("validation-user",
					List.of("MONDAY_9", invalidSlot))).isInstanceOf(IllegalArgumentException.class);
			assertThat(availabilityService.getAvailability("validation-user"))
					.containsExactly("WEDNESDAY_12");
		}
	}

}
