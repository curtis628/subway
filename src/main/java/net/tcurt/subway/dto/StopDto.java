package net.tcurt.subway.dto;

import net.tcurt.subway.entity.Stop;

public record StopDto(String id, String name, double latitude, double longitude) {
  public static StopDto fromEntity(Stop stop) {
    return new StopDto(stop.getId(), stop.getName(), stop.getLatitude(), stop.getLongitude());
  }
}
