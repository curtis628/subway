package net.tcurt.subway.controller.v1;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
public class StopControllerV1Test {

  @Autowired private MockMvc mockMvc;
  @Autowired private SubwayService subwayService;

  @BeforeEach
  void setup() {
    Stop stop1 = Stop.builder().id("AB").name("AB stop").latitude(1.0).longitude(1.0).build();
    Stop stop2 = Stop.builder().id("BC").name("BC stop").latitude(2.0).longitude(2.0).build();
    Stop stop3 = Stop.builder().id("CD").name("CD stop").latitude(3.0).longitude(3.0).build();

    subwayService.getOrCreateStop(stop1);
    subwayService.getOrCreateStop(stop2);
    subwayService.getOrCreateStop(stop3);
  }

  @Test
  void getAll() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[?(@.id == 'AB')]").exists())
        .andExpect(jsonPath("$[?(@.id == 'AB')].name").value("AB stop"))
        .andExpect(jsonPath("$[?(@.id == 'AB')].latitude").value(1.0))
        .andExpect(jsonPath("$[?(@.id == 'AB')].longitude").value(1.0));
  }

  @Test
  void getAll_filterName() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops?name=bc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[?(@.id == 'BC')]").exists())
        .andExpect(jsonPath("$[?(@.id == 'BC')].name").value("BC stop"))
        .andExpect(jsonPath("$[?(@.id == 'BC')].latitude").value(2.0))
        .andExpect(jsonPath("$[?(@.id == 'BC')].longitude").value(2.0));
  }

  @Test
  void getOne() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops/CD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is("CD")))
        .andExpect(jsonPath("$.name", is("CD stop")))
        .andExpect(jsonPath("$.latitude", is(3.0)))
        .andExpect(jsonPath("$.longitude", is(3.0)));
  }

  @Test
  void getOne_notFound() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops/not-there"))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Stop not found: not-there"));
  }
}
