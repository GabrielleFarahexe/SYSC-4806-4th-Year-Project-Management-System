package _thYearProjectManagamentSystemGroup22.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilityRepo extends JpaRepository<Availability, Long> {
    List<Availability> findAllByUsername(String username);

    void deleteAllByUsername(String username);
}
