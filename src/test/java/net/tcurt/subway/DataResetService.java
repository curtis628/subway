package net.tcurt.subway;

import lombok.RequiredArgsConstructor;
import net.tcurt.subway.repository.ConnectionRepository;
import net.tcurt.subway.repository.LineRepository;
import net.tcurt.subway.repository.StopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Easily clean out in-memory DB between unit test runs. */
@Service
@RequiredArgsConstructor
public class DataResetService {

  private final ConnectionRepository connectionRepository;
  private final LineRepository lineRepository;
  private final StopRepository stopRepository;

  @Transactional
  public void deleteAllData() {
    connectionRepository.deleteAllInBatch(); // Most dependent, delete first
    lineRepository.deleteAllInBatch(); // Less dependent, delete second
    stopRepository.deleteAllInBatch(); // No dependencies, delete last
  }
}
