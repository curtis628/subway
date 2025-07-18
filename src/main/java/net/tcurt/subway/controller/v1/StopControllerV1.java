package net.tcurt.subway.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Stops", description = "Subway stop API")
@RequiredArgsConstructor
public class StopControllerV1 {

  private final SubwayService subwayService;

  @GetMapping
  @Operation(
      summary = "Query all stops",
      description =
          """
          Returns a collection of subway stops. If `name` is provided, results are filtered by
          name (case-insensitive substring match).
          """)
  Collection<StopDto> list(
      @Parameter(
              description =
                  """
                  Optional case-insensitive name filter. Returns stops where the name contains this value.
                  """,
              example = "place-north")
          @RequestParam(required = false)
          String name) {
    return subwayService.getStops(name);
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get specific stop",
      description =
          "Returns the subway stop matching the provided stop ID. If the stop does not exist, a 404"
              + " response is returned.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Stop found and returned"),
        @ApiResponse(responseCode = "404", description = "Stop not found")
      })
  StopDto one(@PathVariable String id) {
    return subwayService.getStopDto(id);
  }
}
