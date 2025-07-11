package net.tcurt.subway.repository;

import java.util.Optional;
import net.tcurt.subway.entity.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
  Optional<Line> findByName(String name);
}
