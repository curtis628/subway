package net.tcurt.subway;

import static org.assertj.core.api.Assertions.assertThat;

import net.tcurt.subway.controller.v1.StopControllerV1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SmokeTest {

  @Autowired private StopControllerV1 stopControllerV1;

  @Test
  void contextLoads() throws Exception {
    assertThat(stopControllerV1).isNotNull();
  }
}
