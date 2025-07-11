package net.tcurt.subway.repository;

import java.util.List;
import net.tcurt.subway.entity.Connection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
  List<Connection> findByFromId(String fromStopId, Sort sort);
}
