package _thYearProjectManagamentSystemGroup20;

import _thYearProjectManagamentSystemGroup20.Availability.AvailabilityController;
import _thYearProjectManagamentSystemGroup20.Availability.AvailabilityService;
import _thYearProjectManagamentSystemGroup20.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AvailabilityController: only the web layer is loaded, and
 * AvailabilityService is a Mockito mock.
 */
@WebMvcTest(AvailabilityController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "student", roles = "STUDENT")
class AvailabilityControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    @Test
    void showAvailabilityLoadsSlotsForLoggedInUser() throws Exception {
        when(availabilityService.getAvailability("student")).thenReturn(List.of("MONDAY_9"));

        mockMvc.perform(get("/availability"))
                .andExpect(status().isOk())
                .andExpect(view().name("availability"))
                .andExpect(model().attribute("days", List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)))
                .andExpect(model().attribute("hours", List.of(9, 10, 11, 12, 13, 14, 15)))
                .andExpect(model().attribute("selectedSlots", List.of("MONDAY_9")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"STUDENT", "PROFESSOR", "COORDINATOR"})
    void studentAvailabilityKeepsStudentNavigationForMultiRoleUser() throws Exception {
        mockMvc.perform(get("/student/availability"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Browse Projects")))
                .andExpect(content().string(not(containsString("Add Project"))))
                .andExpect(content().string(containsString("action=\"/student/availability\"")));

        mockMvc.perform(post("/student/availability").with(csrf()))
                .andExpect(redirectedUrl("/student/availability"));

        mockMvc.perform(get("/professor/availability"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add Project")));
    }

    @Test
    void saveAvailabilityPassesSlotsToService() throws Exception {
        mockMvc.perform(post("/availability").with(csrf())
                        .param("slots", "MONDAY_9", "FRIDAY_15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/availability"))
                .andExpect(flash().attribute("success", "Availability saved successfully!"));

        verify(availabilityService).saveAvailability("student", List.of("MONDAY_9", "FRIDAY_15"));
    }

    @Test
    void saveWithNoSlotsPassesEmptyList() throws Exception {
        mockMvc.perform(post("/availability").with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(availabilityService).saveAvailability("student", List.of());
    }

    @Test
    void invalidSlotReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("bad slot"))
                .when(availabilityService).saveAvailability(eq("student"), anyList());

        mockMvc.perform(post("/availability").with(csrf())
                        .param("slots", "SUNDAY_3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postWithoutCsrfIsRejected() throws Exception {
        mockMvc.perform(post("/availability").param("slots", "MONDAY_9"))
                .andExpect(status().isForbidden());

        verify(availabilityService, never()).saveAvailability(any(), anyList());
    }

    @Test
    @WithMockUser(roles = "COORDINATOR")
    void coordinatorCannotAccessAvailability() throws Exception {
        mockMvc.perform(get("/availability"))
                .andExpect(status().isForbidden());
    }
}
