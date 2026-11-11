package _thYearProjectManagamentSystemGroup22;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Professor portal: dashboard and "Add Project" form.
 * (Manage Projects, Applications and Availability pages can be added
 * to this controller or to their own controllers under /professor.)
 */
@Controller
@RequestMapping("/professor")
public class ProfessorController {

    private final ProjectRepository projectRepository;

    public ProfessorController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /** Program list for the restrictions dropdown, available to every view of this controller. */
    @ModelAttribute("programs")
    public Program[] programs() {
        return Program.values();
    }

    // ====== DASHBOARD =======

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProjects", projectRepository.countByStatusNot(ProjectStatus.DELETED));
        model.addAttribute("activeProjects", projectRepository.countByStatus(ProjectStatus.ACTIVE));
        // TODO: replace with the real counts once the Application and Presentation
        //       entities/repositories exist (owned by other team members).
        model.addAttribute("totalApplications", 0L);
        model.addAttribute("upcomingPresentations", 0L);
        model.addAttribute("recentProjects",
                projectRepository.findTop5ByStatusNotOrderByCreatedAtDesc(ProjectStatus.DELETED));
        return "professor/dashboard";
    }

    // -===== Add Project =======

    @GetMapping("/projects/new")
    public String newProjectForm(Model model) {
        model.addAttribute("project", new Project());
        return "professor/project-form";
    }

    @PostMapping("/projects")
    public String createProject(@Valid @ModelAttribute("project") Project project,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "professor/project-form"; // re-show the form with error messages
        }
        // Never trust these from the form
        project.setId(null);
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedAt(null);

        Project saved = projectRepository.save(project);
        redirectAttributes.addFlashAttribute("successMessage",
                "Project \"" + saved.getTitle() + "\" was created.");
        return "redirect:/professor";
    }
}