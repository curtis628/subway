package net.tcurt.subway.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tcurt.subway.dto.ConnectionDto;
import net.tcurt.subway.dto.StopDto;
import net.tcurt.subway.entity.Connection;
import net.tcurt.subway.entity.Line;
import net.tcurt.subway.entity.Stop;
import net.tcurt.subway.repository.ConnectionRepository;
import net.tcurt.subway.repository.LineRepository;
import net.tcurt.subway.repository.StopRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubwayService {
  private final LineRepository lineRepository;
  private final StopRepository stopRepository;
  private final ConnectionRepository connectionRepository;

  public List<Line> getLines() {
    List<Line> response = lineRepository.findAll();
    log.debug("Returning {} lines: {}", response.size(), response);
    return response;
  }

  public Line getLine(String id) {
    Line line =
        lineRepository
            .findByName(id)
            .orElseThrow(() -> new EntityNotFoundException("Line not found: " + id));
    log.debug("Found line: {}", line);
    return line;
  }

  public Line createLine(Line line) {
    Line result = lineRepository.save(line);
    log.debug("Creating line: {}", result);
    return result;
  }

  public List<StopDto> getStops() {
    return getStops(null);
  }

  public List<StopDto> getStops(String name) {
    List<Stop> response;
    if (name != null && !name.isBlank()) {
      response = stopRepository.findByNameContainingIgnoreCase(name);
    } else {
      response = stopRepository.findAll();
    }

    log.debug("Returning {} stops: {}", response.size(), response);
    return response.stream().map(StopDto::fromEntity).collect(Collectors.toList());
  }

  public Stop getStop(String id) {
    Stop stop =
        stopRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Stop not found: " + id));
    log.debug("Found stop: {}", stop);
    return stop;
  }

  public StopDto getStopDto(String id) {
    return StopDto.fromEntity(getStop(id));
  }

  public Stop getOrCreateStop(Stop stop) {
    Stop result = stopRepository.findById(stop.getId()).orElse(null);
    if (result == null) {
      result = stopRepository.save(stop);
      log.info("Created stop: {}", result);
    }
    return result;
  }

  public Connection getOrCreateConnection(Stop from, Stop to, Line line) {
    Connection c = Connection.builder().from(from).to(to).line(line).build();
    Connection result;
    try {
      result = connectionRepository.save(c);
      log.debug("Created connection: {}", result);
    } catch (DataIntegrityViolationException ex) {
      log.debug("Duplicate connection for {} + {} + {}: {}", from, to, line, ex.getMessage());
      result = c;
    }
    return result;
  }

  public List<ConnectionDto> getConnectionsFromStop(String stopId) {
    stopRepository
        .findById(stopId)
        .orElseThrow(() -> new EntityNotFoundException("stop not found: " + stopId));
    List<Connection> connections =
        connectionRepository.findByFromId(stopId, Sort.by("line.name").ascending());
    return connections.stream().map(ConnectionDto::fromEntity).collect(Collectors.toList());
  }
}
