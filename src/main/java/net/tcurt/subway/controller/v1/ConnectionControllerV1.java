package net.tcurt.subway.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Connections", description = "Operations related to connections between subway stops")
@RequiredArgsConstructor
public class ConnectionControllerV1 {

  private final SubwayService subwayService;

  @GetMapping
  @Operation(
      summary = "Get outbound connections from a stop",
      description =
          """
          Returns all outbound connections from the stop identified by `stop_id`.
          Each connection includes:
          - the subway line (e.g., "Orange")
          - the destination stop (id, name, latitude, longitude)
          """)
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Successfully returned connections"),
        @ApiResponse(responseCode = "400", description = "Missing or invalid stop_id"),
        @ApiResponse(responseCode = "404", description = "Stop not found")
      })
  Collection<ConnectionDto> list(
      @Parameter(
              name = "stop_id",
              description = "The ID of the stop to retrieve outbound connections from.",
              example = "place-north",
              required = true)
          @RequestParam(name = "stop_id")
          String stopId) {
    return subwayService.getConnectionsFromStop(stopId);
  }
}
