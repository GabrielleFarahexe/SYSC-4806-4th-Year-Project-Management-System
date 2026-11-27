package _thYearProjectManagamentSystemGroup20.Availability.internal;
import org.springframework.data.jpa.repository.JpaRepository;

import _thYearProjectManagamentSystemGroup20.Availability.Availability;

import java.util.List;

public interface AvailabilityRepo extends JpaRepository<Availability, Long> {
    List<Availability> findAllByUsername(String username);

    void deleteAllByUsername(String username);
}
