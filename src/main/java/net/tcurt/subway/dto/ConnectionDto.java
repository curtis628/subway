package net.tcurt.subway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.tcurt.subway.entity.Connection;

@Schema(
    name = "Connection",
    description = "Connection between two subway stops via a specific line")
public record ConnectionDto(
    @Schema(description = "The connection's line", example = "Orange") String line,
    @Schema(description = "Destination stop") StopDto to) {
  public static ConnectionDto fromEntity(Connection c) {
    return new ConnectionDto(c.getLine().getName(), StopDto.fromEntity(c.getTo()));
  }
}
