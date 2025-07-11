package net.tcurt.subway.dto;

import net.tcurt.subway.entity.Connection;

public record ConnectionDto(String line, StopDto to) {
  public static ConnectionDto fromEntity(Connection c) {
    return new ConnectionDto(c.getLine().getName(), StopDto.fromEntity(c.getTo()));
  }
}
