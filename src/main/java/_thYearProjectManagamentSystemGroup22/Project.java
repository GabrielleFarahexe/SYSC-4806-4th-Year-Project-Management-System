package _thYearProjectManagamentSystemGroup22;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

/** A 4th year project topic offered by a professor. */
@Entity
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be at most 2000 characters")
    @Column(length = 2000)
    private String description;

    /** Empty set = open to all programs. */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Program> programRestrictions = EnumSet.noneOf(Program.class);

    @NotNull(message = "Number of students is required")
    @Min(value = 1, message = "At least 1 student is required")
    @Max(value = 10, message = "At most 10 students")
    private Integer requiredStudents;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    private LocalDateTime createdAt;

    public Project() {
    }

    public Project(String title, String description, Set<Program> programRestrictions, Integer requiredStudents) {
        this.title = title;
        this.description = description;
        setProgramRestrictions(programRestrictions);
        this.requiredStudents = requiredStudents;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public boolean isOpenToAllPrograms() {
        return programRestrictions == null || programRestrictions.isEmpty();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Program> getProgramRestrictions() { return programRestrictions; }
    public void setProgramRestrictions(Set<Program> programRestrictions) {
        this.programRestrictions = (programRestrictions == null || programRestrictions.isEmpty())
                ? EnumSet.noneOf(Program.class)
                : EnumSet.copyOf(programRestrictions);
    }

    public Integer getRequiredStudents() { return requiredStudents; }
    public void setRequiredStudents(Integer requiredStudents) { this.requiredStudents = requiredStudents; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}