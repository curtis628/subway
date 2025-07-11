package net.tcurt.subway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"from_id", "to_id", "line_name"}))
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Connection {

  private @Id @GeneratedValue Long id;

  @ManyToOne
  @JoinColumn(name = "line_name")
  private Line line;

  @ManyToOne
  @JoinColumn(name = "from_id")
  private Stop from;

  @ManyToOne
  @JoinColumn(name = "to_id")
  private Stop to;
}
