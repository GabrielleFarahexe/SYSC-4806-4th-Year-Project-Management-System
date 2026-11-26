package _thYearProjectManagamentSystemGroup20.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    // Logged in -> your dashboard. Not logged in -> login page.
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    // Public page explaining the system
    @GetMapping("/about")
    public String about() {
        return "index";
    }

    @GetMapping("/{role:student|professor|coordinator}/about")
    public String dashboardAbout(@PathVariable String role, Model model) {
        model.addAttribute("dashboardRole", role);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // After login, send each user to the dashboard for their role
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        // Only look at roles (ROLE_...). Spring Security also adds other authorities on
        // login.
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_COORDINATOR")) {
            return "redirect:/coordinator/dashboard";
        }
        if (roles.contains("ROLE_PROFESSOR")) {
            return "redirect:/professor";
        }
        return "redirect:/student/dashboard";
    }
}