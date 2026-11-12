package _thYearProjectManagamentSystemGroup22.Availability;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/availability")
@PreAuthorize("hasAnyRole('STUDENT', 'PROFESSOR')")
public class AvailabilityController {

    private final AvailabilityService service;

    public AvailabilityController(AvailabilityService service) {
        this.service = service;
    }

    @GetMapping
    public String showAvailability(
            Model model,
            Authentication authentication) {

        String username = authentication.getName();

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

        return "redirect:/availability";
    }
}
