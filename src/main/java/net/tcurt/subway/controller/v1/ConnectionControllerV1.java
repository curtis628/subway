package net.tcurt.subway.controller.v1;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import net.tcurt.subway.dto.ConnectionDto;
import net.tcurt.subway.service.SubwayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/connections")
@RequiredArgsConstructor
public class ConnectionControllerV1 {

  private final SubwayService subwayService;

  @GetMapping
  Collection<ConnectionDto> list(@RequestParam(name = "stop_id", required = true) String stopId) {
    return subwayService.getConnectionsFromStop(stopId);
  }
}
