package net.tcurt.subway.repository;

import java.util.List;
import java.util.Optional;
import net.tcurt.subway.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopRepository extends JpaRepository<Stop, Long> {
  Optional<Stop> findById(String id);

  List<Stop> findByNameContainingIgnoreCase(String name);
}
