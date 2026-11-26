package _thYearProjectManagamentSystemGroup20.Availability;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping({"/availability", "/{role:student|professor}/availability"})
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @GetMapping
    public String showAvailability(
            @PathVariable(name = "role", required = false) String role,
            Model model,
            Authentication authentication) {

        String username = authentication.getName();

        String sidebarRole = role;
        if (sidebarRole == null) {
            sidebarRole = AuthorityUtils.authorityListToSet(authentication.getAuthorities())
                    .contains("ROLE_PROFESSOR") ? "professor" : "student";
        }
        model.addAttribute("role", sidebarRole);
        model.addAttribute("availabilityPath", availabilityPath(role));

        model.addAttribute("days", List.of(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
        ));

        model.addAttribute("hours",
                IntStream.range(9, 16).boxed().toList());

        model.addAttribute("selectedSlots",
                service.getAvailability(username));

        return "availability";
    }

    @PostMapping
    public String saveAvailability(
            @PathVariable(name = "role", required = false) String role,
            @RequestParam(name = "slots", required = false)
            List<String> slots,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        try {
            service.saveAvailability(username, slots == null ? List.of() : slots);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid availability slots", exception);
        }

        redirectAttributes.addFlashAttribute(
                "success",
                "Availability saved successfully!"
        );

        return "redirect:" + availabilityPath(role);
    }

    private String availabilityPath(String role) {
        return role == null ? "/availability" : "/" + role + "/availability";
    }
}
