package _thYearProjectManagamentSystemGroup20.model;

import jakarta.persistence.*;

@Entity
@Table(name="Students")

public class Student {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String studentNumber;
    private String name;
    private String email;
    private boolean assigned;

    public Student() {}

    public Student(Long id, String studentNumber, String name, String email, boolean assigned) {
        this.studentNumber = studentNumber;
        this.name = name;
        this.email = email;
        this.assigned = assigned;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}
