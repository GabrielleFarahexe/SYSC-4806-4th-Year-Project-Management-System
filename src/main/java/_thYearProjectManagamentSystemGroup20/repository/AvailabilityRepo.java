package _thYearProjectManagamentSystemGroup20.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import _thYearProjectManagamentSystemGroup20.model.Availability;

public interface AvailabilityRepo extends JpaRepository<Availability, Long> {
    List<Availability> findAllByUsername(String username);

    void deleteAllByUsername(String username);
}
