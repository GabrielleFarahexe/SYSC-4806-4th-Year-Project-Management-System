package _thYearProjectManagamentSystemGroup20.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Demo accounts, one per role. {noop} means the password isn't encrypted (fine
    // for a class demo).
    @Bean
    public UserDetailsService users() {
        UserDetails student = User.withUsername("student")
                .password("{noop}student123").roles("STUDENT").build();
        UserDetails professor = User.withUsername("professor")
                .password("{noop}professor123").roles("PROFESSOR").build();
        UserDetails coordinator = User.withUsername("coordinator")
                .password("{noop}coordinator123").roles("COORDINATOR").build();
        UserDetails admin = User.withUsername("admin")
                .password("{noop}group20demo").roles("STUDENT", "PROFESSOR", "COORDINATOR").build();

        return new InMemoryUserDetailsManager(student, professor, coordinator, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/about", "/css/**").permitAll()
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/professor/**").hasRole("PROFESSOR")
                        .requestMatchers("/coordinator/**").hasRole("COORDINATOR")
                        .requestMatchers("/availability/**").hasAnyRole("STUDENT", "PROFESSOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}