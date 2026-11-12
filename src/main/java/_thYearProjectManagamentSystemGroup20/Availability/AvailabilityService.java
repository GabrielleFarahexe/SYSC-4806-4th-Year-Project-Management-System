package _thYearProjectManagamentSystemGroup20.Availability;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
public class AvailabilityService {
    private final AvailabilityRepo repository;

    public AvailabilityService(AvailabilityRepo repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<String> getAvailability(String username) {
        return repository.findAllByUsername(username).stream()
                .map(slot -> slot.getDayOfWeek().name() + "_" + slot.getStartHour())
                .toList();
    }

    @Transactional
    public void saveAvailability(String username, List<String> slots) {
        // Validate every slot before replacing the user's existing selection.
        List<Availability> availability = slots.stream().distinct().map(slot -> {
            if (slot == null || !slot.matches("(MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY)_(9|1[0-5])")) {
                throw new IllegalArgumentException("Invalid availability slot: " + slot);
            }
            String[] parts = slot.split("_");
            return Availability.createAvailability(username, DayOfWeek.valueOf(parts[0]),
                    Integer.parseInt(parts[1]));
        }).toList();

        repository.deleteAllByUsername(username);
        repository.saveAll(availability);
    }
}
