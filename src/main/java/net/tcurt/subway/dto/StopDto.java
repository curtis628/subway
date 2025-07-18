package net.tcurt.subway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.tcurt.subway.entity.Stop;

@Schema(name = "Stop", description = "Represents a subway stop")
public record StopDto(
    @Schema(description = "Unique stop ID", example = "place-haecl") String id,
    @Schema(description = "Stop name", example = "Haymarket") String name,
    @Schema(description = "Latitude of the stop", example = "42.363021") double latitude,
    @Schema(description = "Longitude of the stop", example = "-71.05829") double longitude) {
  public static StopDto fromEntity(Stop stop) {
    return new StopDto(stop.getId(), stop.getName(), stop.getLatitude(), stop.getLongitude());
  }
}
