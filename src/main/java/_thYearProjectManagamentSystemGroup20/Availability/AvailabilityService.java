package _thYearProjectManagamentSystemGroup20.Availability;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import _thYearProjectManagamentSystemGroup20.repository.ProjectRepository;

import _thYearProjectManagamentSystemGroup20.model.Project;
import java.util.List;

@Service
public class AvailabilityService {

    private final ProjectRepository projects;

    public AvailabilityService(
            ProjectRepository projects) {
        this.projects = projects;
    }

    public List<String> reservedSlots(String professor) {
        return projects.reservedSlots(professor);
    }

    private List<String> available(java.util.Set<String> slots, String professor) {
        var reserved = reservedSlots(professor);
        return slots.stream().filter(slot -> !reserved.contains(slot)).toList();
    }

    public List<String> commonSlots(Project project) {
        return available(project.getPresentationSlots(), project.getProfessorUsername()).stream()
                .filter(project.getOfferedSlots()::contains).toList();
    }

    @Transactional
    public void schedule(Long projectId, String slot) {
        Project project = projects.lockProject(projectId).orElseThrow(() ->
                new IllegalArgumentException("Project no longer exists."));
        if (project.getStatus() != _thYearProjectManagamentSystemGroup20.model.ProjectStatus.ACTIVE ||
                !commonSlots(project).contains(slot)) {
            throw new IllegalArgumentException("This time is no longer available for scheduling.");
        }
        project.schedulePresentation(slot);
    }

    @Transactional(readOnly = true)
    public List<String> allowedSlots(String student) {
        return projects.findByMemberUsernamesContaining(student)
                .map(project -> available(project.getOfferedSlots(), project.getProfessorUsername())).orElse(List.of());
    }

    @Transactional(readOnly = true)
    public List<String> teamSlots(String student) {
        return projects.findByMemberUsernamesContaining(student)
                .map(project -> commonSlots(project)).orElse(List.of());
    }

    @Transactional
    public void saveTeamSlots(String student, List<String> slots) {
        var project = projects.findByMemberUsernamesContaining(student)
                .orElseThrow(() -> new IllegalArgumentException("No project assigned"));
        project = projects.lockProject(project.getId()).orElseThrow();
        if (!available(project.getOfferedSlots(), project.getProfessorUsername()).containsAll(slots)) {
            throw new IllegalArgumentException("Professor is unavailable");
        }
        project.getPresentationSlots().clear();
        project.getPresentationSlots().addAll(slots);
    }

    public List<Project> professorProjects(String username) {
        return projects.findByProfessorUsernameOrderByIdAsc(username);
    }

    private Project ownedProject(String username, Long id) {
        return projects.findByIdAndProfessorUsername(id, username)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Project not found"));
    }

    @Transactional(readOnly = true)
    public List<String> getAvailability(String username, Long projectId) {
        return available(ownedProject(username, projectId).getOfferedSlots(), username);
    }

    @Transactional
    public void saveAvailability(String username, Long projectId, List<String> slots) {
        Project project = ownedProject(username, projectId);
        projects.lockProject(project.getId()).orElseThrow();
        if (!java.util.Collections.disjoint(reservedSlots(username), slots)) {
            throw new IllegalArgumentException("Time already scheduled");
        }
        for (String slot : slots) {
            String[] parts = slot == null ? new String[0] : slot.split("_", -1);
            if (parts.length != 2 || !parts[1].matches("9|1[0-5]")) {
                throw new IllegalArgumentException("Invalid availability slot");
            }
            Weekday.valueOf(parts[0]);
        }
        project.getOfferedSlots().clear();
        project.getOfferedSlots().addAll(slots);
        project.getPresentationSlots().retainAll(slots);
    }
}
