package _thYearProjectManagamentSystemGroup20.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // After login, send each user to the dashboard for their role
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.size() > 1) {
            return "redirect:/"; // admin has every role, so show the landing page
        }
        if (roles.contains("ROLE_PROFESSOR")) {
            return "redirect:/professor";
        }
        if (roles.contains("ROLE_COORDINATOR")) {
            return "redirect:/coordinator/dashboard";
        }
        return "redirect:/student/dashboard";
    }
}