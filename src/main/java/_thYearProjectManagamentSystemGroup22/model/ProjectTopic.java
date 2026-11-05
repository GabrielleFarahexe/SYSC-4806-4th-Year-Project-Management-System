package _thYearProjectManagamentSystemGroup22.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "Project_Topics")
public class ProjectTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String description;

    @NotBlank
    @Column(nullable = false)
    private String programRestrictions;

    @Min(1)
    @Column(nullable = false)
    private Integer requiredStudents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectTopicStatus status = ProjectTopicStatus.ACTIVE;

    public ProjectTopic() {

    }

    public ProjectTopic(String title, String description, String programRestrictions, Integer requiredStudents) {
        this.title = title;
        this.description = description;
        this.programRestrictions = programRestrictions;
        this.requiredStudents = requiredStudents;
        this.status = ProjectTopicStatus.ACTIVE;

    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProgramRestrictions() {
        return programRestrictions;
    }

    public void setProgramRestrictions(String programRestrictions) {
        this.programRestrictions = programRestrictions;
    }

    public Integer getRequiredStudents() {
        return requiredStudents;
    }

    public void setRequiredStudents(Integer requiredStudents) {
        this.requiredStudents = requiredStudents;
    }

    public ProjectTopicStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectTopicStatus status) {
        this.status = status;
    }

}
