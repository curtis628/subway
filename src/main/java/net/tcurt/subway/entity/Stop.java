package net.tcurt.subway.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Stop {

  private @Id String id;
  private String name;

  @ToString.Exclude private double latitude;

  @ToString.Exclude private double longitude;
}
