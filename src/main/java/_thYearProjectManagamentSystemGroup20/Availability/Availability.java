package _thYearProjectManagamentSystemGroup20.Availability;
import jakarta.persistence.*;
import java.time.DayOfWeek;

@Entity
@Table(name = "availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username",nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week",nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_hour", nullable = false)
    private int startHour;

    protected  Availability(){}

    private Availability(String username, DayOfWeek dayOfWeek){
        this.username = username;
        this.dayOfWeek = dayOfWeek;
    }

    public static Availability createAvailability(String username, DayOfWeek dayOfWeek){
        return new Availability(username, dayOfWeek);
    }

    public static Availability createAvailability(String username, DayOfWeek dayOfWeek, int startHour) {
        Availability availability = new Availability(username, dayOfWeek);
        availability.startHour = startHour;
        return availability;
    }

    public int getStartHour() {
        return startHour;
    }

    public String getUsername(){
        return username;
    }

    public DayOfWeek getDayOfWeek(){
        return dayOfWeek;
    }

    public Long getId(){
        return id;
    }
}
