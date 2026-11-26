package _thYearProjectManagamentSystemGroup20.controller;

import _thYearProjectManagamentSystemGroup20.Availability.AvailabilityService;
import _thYearProjectManagamentSystemGroup20.model.ProjectStatus;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import _thYearProjectManagamentSystemGroup20.Availability.Weekday;
import java.util.List;
import java.util.HashMap;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/coordinator/presentations")
public class PresentationController {
    private final ProjectRepository projects;
    private final AvailabilityService availability;

    public PresentationController(ProjectRepository projects, AvailabilityService availability) {
        this.projects = projects;
        this.availability = availability;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String list(Model model) {
        var active = projects.findByStatusOrderByTitleAsc(ProjectStatus.ACTIVE);
        var common = new HashMap<Long, List<String>>();
        for (var project : active) {
            common.put(project.getId(), availability.commonSlots(project));
        }
        model.addAttribute("projects", active);
        model.addAttribute("common", common);
        model.addAttribute("days", List.of(Weekday.values()));
        model.addAttribute("hours", IntStream.range(9, 16).boxed().toList());
        return "coordinator/presentations";
    }

    @PostMapping("/{id}")
    public String schedule(@PathVariable Long id, @RequestParam String slot, RedirectAttributes redirect) {
        try {
            availability.schedule(id, slot);
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            redirect.addFlashAttribute("error", "The professor already has a presentation at that time.");
        }
        return "redirect:/coordinator/presentations";
    }
}
