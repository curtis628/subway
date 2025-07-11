package net.tcurt.subway.controller.v1;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import net.tcurt.subway.entity.Connection;
import net.tcurt.subway.entity.Line;
import net.tcurt.subway.entity.Stop;
import net.tcurt.subway.service.SubwayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ConnectionControllerV1Test {

  @Autowired private MockMvc mockMvc;
  @Autowired private SubwayService subwayService;
  @Autowired private ObjectMapper mapper;

  /**
   * Connections are as below. Vertical connections ({@code |}) are associated with the {@code
   * vertical} {@link Line}. Horizontal connections ({@code -}) are associated with the {@code
   * horizontal} {@link Line}.
   *
   * <pre>
   *     A
   *     |
   *     B - D
   *     |
   *     C
   * </pre>
   */
  @BeforeEach
  void setup() {
    Line vertical = new Line("vertical");
    Line horizontal = new Line("horizontal");
    subwayService.createLine(vertical);
    subwayService.createLine(horizontal);

    Stop stopA = Stop.builder().id("A").name("A stop").latitude(1.0).longitude(1.0).build();
    Stop stopB = Stop.builder().id("B").name("B stop").latitude(2.0).longitude(2.0).build();
    Stop stopC = Stop.builder().id("C").name("C stop").latitude(3.0).longitude(3.0).build();
    Stop stopD = Stop.builder().id("D").name("D stop").latitude(4.0).longitude(4.0).build();
    Arrays.asList(stopA, stopB, stopC, stopD).forEach(subwayService::getOrCreateStop);

    Connection ab = Connection.builder().from(stopA).to(stopB).line(vertical).build();
    Connection ba = Connection.builder().from(stopB).to(stopA).line(vertical).build();
    Connection bc = Connection.builder().from(stopB).to(stopC).line(vertical).build();
    Connection cb = Connection.builder().from(stopC).to(stopB).line(vertical).build();
    Connection bd = Connection.builder().from(stopB).to(stopD).line(horizontal).build();
    Connection db = Connection.builder().from(stopD).to(stopB).line(horizontal).build();
    Arrays.asList(ab, ba, bc, cb, bd, db)
        .forEach(c -> subwayService.getOrCreateConnection(c.getFrom(), c.getTo(), c.getLine()));
  }

  @Test
  void get_stopIdMissing() throws Exception {
    String errMsg = "Required parameter 'stop_id' is not present.";
    mockMvc
        .perform(get("/api/v1/connections"))
        .andExpect(status().isBadRequest())
        .andExpect(r -> r.getResolvedException().getMessage().contains(errMsg));
  }

  @Test
  void get_stopIdNotFound() throws Exception {
    mockMvc
        .perform(get("/api/v1/connections?stop_id=not-there"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("stop not found: not-there"));
  }

  @Test
  void getConnectionsForA() throws Exception {
    mockMvc
        .perform(get("/api/v1/connections?stop_id=A"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].line", is("vertical")))
        .andExpect(jsonPath("$[0].to.id", is("B")))
        .andExpect(jsonPath("$[0].to.name", is("B stop")))
        .andExpect(jsonPath("$[0].to.latitude", is(2.0)))
        .andExpect(jsonPath("$[0].to.longitude", is(2.0)));
  }

  @Test
  void getConnectionsForB() throws Exception {
    mockMvc
        .perform(get("/api/v1/connections?stop_id=B"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[?(@.line == 'vertical')]", hasSize(2)))
        .andExpect(jsonPath("$[?(@.line == 'vertical')].to.id", hasItems("A", "C")))
        .andExpect(jsonPath("$[?(@.line == 'vertical')].to.name", hasItems("A stop", "C stop")))
        .andExpect(jsonPath("$[?(@.line == 'vertical')].to.longitude", hasItems(1.0, 3.0)))
        .andExpect(jsonPath("$[?(@.line == 'vertical')].to.latitude", hasItems(1.0, 3.0)))
        .andExpect(jsonPath("$[?(@.line == 'horizontal')]", hasSize(1)))
        .andExpect(jsonPath("$[?(@.line == 'horizontal')].to.id", hasItem("D")));
  }

  @Test
  void getConnectionsForC() throws Exception {
    mockMvc
        .perform(get("/api/v1/connections?stop_id=C"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].line", is("vertical")))
        .andExpect(jsonPath("$[0].to.id", is("B")))
        .andExpect(jsonPath("$[0].to.name", is("B stop")))
        .andExpect(jsonPath("$[0].to.latitude", is(2.0)))
        .andExpect(jsonPath("$[0].to.longitude", is(2.0)));
  }

  @Test
  void getConnectionsForD() throws Exception {
    mockMvc
        .perform(get("/api/v1/connections?stop_id=D"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].line", is("horizontal")))
        .andExpect(jsonPath("$[0].to.id", is("B")))
        .andExpect(jsonPath("$[0].to.name", is("B stop")))
        .andExpect(jsonPath("$[0].to.latitude", is(2.0)))
        .andExpect(jsonPath("$[0].to.longitude", is(2.0)));
  }
}
