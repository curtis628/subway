package net.tcurt.subway.controller.v1;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import net.tcurt.subway.dto.StopDto;
import net.tcurt.subway.service.SubwayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stops")
@RequiredArgsConstructor
public class StopControllerV1 {

  private final SubwayService subwayService;

  @GetMapping
  Collection<StopDto> list(@RequestParam(required = false) String name) {
    return subwayService.getStops(name);
  }

  @GetMapping("/{id}")
  StopDto one(@PathVariable String id) {
    return subwayService.getStopDto(id);
  }
}
