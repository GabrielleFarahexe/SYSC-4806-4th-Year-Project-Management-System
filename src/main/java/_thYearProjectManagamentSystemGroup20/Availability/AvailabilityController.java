package _thYearProjectManagamentSystemGroup20.Availability;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

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
            @RequestParam(required = false) Long projectId,
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

        model.addAttribute("days", List.of(Weekday.values()));

        model.addAttribute("hours",
                IntStream.range(9, 16).boxed().toList());

        if (sidebarRole.equals("student")) {
            model.addAttribute("selectedSlots", service.teamSlots(username));
            model.addAttribute("allowedSlots", service.allowedSlots(username));
        } else {
            model.addAttribute("reserved", service.reservedSlots(username));
            var projects = service.professorProjects(username);
            if (projectId == null && !projects.isEmpty()) projectId = projects.get(0).getId();
            model.addAttribute("projects", projects);
            model.addAttribute("projectId", projectId);
            model.addAttribute("selectedSlots", projectId == null ? List.of() : service.getAvailability(username, projectId));
        }

        return "availability";
    }

    @PostMapping
    public String saveAvailability(
            @PathVariable(name = "role", required = false) String role,
            @RequestParam(required = false) Long projectId,
            @RequestParam(name = "slots", required = false)
            List<String> slots,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        try {
            boolean professor = role == null ? AuthorityUtils.authorityListToSet(authentication.getAuthorities())
                    .contains("ROLE_PROFESSOR") : role.equals("professor");
            List<String> selection = slots == null ? List.of() : slots;
            if (professor) {
                if (projectId == null) throw new IllegalArgumentException("Choose a project");
                service.saveAvailability(username, projectId, selection);
                redirectAttributes.addAttribute("projectId", projectId);
            }
            else service.saveTeamSlots(username, selection);
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
