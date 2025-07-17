package net.tcurt.subway;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** TODO */
@SpringBootTest
// @ActiveProfiles("filedb") // use file DB
@AutoConfigureMockMvc
public class SubwayITest {

  @Autowired private MockMvc mockMvc;

  @Test
  void downtownCrossing() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops?name=downtown cr"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is("place-dwnxg")))
        .andExpect(jsonPath("$[0].name", is("Downtown Crossing")))
        .andExpect(jsonPath("$[0].latitude", is(42.355518)))
        .andExpect(jsonPath("$[0].longitude", is(-71.060225)))
        .andDo(print());

    mockMvc
        .perform(get("/api/v1/connections?stop_id=place-dwnxg"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(4)))
        .andExpect(jsonPath("$[?(@.line == 'Orange')]", hasSize(2)))
        .andExpect(
            jsonPath("$[?(@.line == 'Orange')].to.id", hasItems("place-state", "place-chncl")))
        .andExpect(jsonPath("$[?(@.line == 'Orange')].to.name", hasItems("State", "Chinatown")))
        .andExpect(jsonPath("$[?(@.line == 'Red')]", hasSize(2)))
        .andExpect(jsonPath("$[?(@.line == 'Red')].to.id", hasItems("place-sstat", "place-pktrm")))
        .andExpect(
            jsonPath("$[?(@.line == 'Red')].to.name", hasItems("South Station", "Park Street")))
        .andDo(print());
  }

  @Test
  void kenmore() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops/place-kencl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.id", is("place-kencl")))
        .andExpect(jsonPath("$.name", is("Kenmore")))
        .andExpect(jsonPath("$.latitude", is(42.348949)))
        .andExpect(jsonPath("$.longitude", is(-71.095169)));

    mockMvc
        .perform(get("/api/v1/connections?stop_id=place-kencl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(6)))
        .andExpect(jsonPath("$[?(@.line == 'Green-B')]", hasSize(2)))
        .andExpect(
            jsonPath("$[?(@.line == 'Green-B')].to.id", hasItems("place-bland", "place-hymnl")))
        .andExpect(
            jsonPath(
                "$[?(@.line == 'Green-B')].to.name",
                hasItems("Blandford Street", "Hynes Convention Center")))
        .andExpect(jsonPath("$[?(@.line == 'Green-C')]", hasSize(2)))
        .andExpect(
            jsonPath("$[?(@.line == 'Green-C')].to.id", hasItems("place-smary", "place-hymnl")))
        .andExpect(
            jsonPath(
                "$[?(@.line == 'Green-C')].to.name",
                hasItems("Saint Mary's Street", "Hynes Convention Center")))
        .andExpect(jsonPath("$[?(@.line == 'Green-D')]", hasSize(2)))
        .andExpect(
            jsonPath("$[?(@.line == 'Green-D')].to.id", hasItems("place-fenwy", "place-hymnl")))
        .andExpect(
            jsonPath(
                "$[?(@.line == 'Green-D')].to.name", hasItems("Fenway", "Hynes Convention Center")))
        .andDo(print());
  }

  @Test
  void wonderland() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops/place-wondl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.id", is("place-wondl")))
        .andExpect(jsonPath("$.name", is("Wonderland")))
        .andExpect(jsonPath("$.latitude", is(42.41342)))
        .andExpect(jsonPath("$.longitude", is(-70.991648)));

    mockMvc
        .perform(get("/api/v1/connections?stop_id=place-wondl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].line", is("Blue")))
        .andExpect(jsonPath("$[0].to.id", is("place-rbmnl")))
        .andExpect(jsonPath("$[0].to.name", is("Revere Beach")))
        .andExpect(jsonPath("$[0].to.latitude", is(42.407843)))
        .andExpect(jsonPath("$[0].to.longitude", is(-70.992533)))
        .andDo(print());
  }

  @Test
  void ashmont() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops/place-asmnl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.id", is("place-asmnl")))
        .andExpect(jsonPath("$.name", is("Ashmont")))
        .andExpect(jsonPath("$.latitude", is(42.28452)))
        .andExpect(jsonPath("$.longitude", is(-71.063777)));

    mockMvc
        .perform(get("/api/v1/connections?stop_id=place-asmnl"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[?(@.line == 'Red')]", hasSize(1)))
        .andExpect(jsonPath("$[?(@.line == 'Red')].to.id", hasItem("place-smmnl")))
        .andExpect(jsonPath("$[?(@.line == 'Red')].to.name", hasItem("Shawmut")))
        .andExpect(jsonPath("$[?(@.line == 'Mattapan')]", hasSize(1)))
        .andExpect(jsonPath("$[?(@.line == 'Mattapan')].to.id", hasItem("place-cedgr")))
        .andExpect(jsonPath("$[?(@.line == 'Mattapan')].to.name", hasItem("Cedar Grove")))
        .andDo(print());
  }

  @Test
  void northStation() throws Exception {
    mockMvc
        .perform(get("/api/v1/stops/place-north"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isMap())
        .andExpect(jsonPath("$.id", is("place-north")))
        .andExpect(jsonPath("$.name", is("North Station")))
        .andExpect(jsonPath("$.latitude", is(42.365577)))
        .andExpect(jsonPath("$.longitude", is(-71.06129)));

    mockMvc
        .perform(get("/api/v1/connections?stop_id=place-north"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(10)))
        .andExpect(jsonPath("$[?(@.line == 'Green-B')]", hasSize(2)))
        .andExpect(jsonPath("$[?(@.line == 'Green-C')]", hasSize(2)))
        .andExpect(jsonPath("$[?(@.line == 'Green-D')]", hasSize(2)))
        .andExpect(jsonPath("$[?(@.line == 'Green-E')]", hasSize(2)))
        // Each Green-* line has the same two stations associated with them
        .andExpect(
            jsonPath("$[?(@.line =~ /^Green.*/)].to.id", hasItems("place-spmnl", "place-haecl")))
        .andExpect(
            jsonPath(
                "$[?(@.line =~ /^Green.*/)].to.name",
                hasItems("Science Park/West End", "Haymarket")))
        .andExpect(jsonPath("$[?(@.line == 'Orange')]", hasSize(2)))
        .andExpect(
            jsonPath("$[?(@.line == 'Orange')].to.id", hasItems("place-ccmnl", "place-haecl")))
        .andExpect(
            jsonPath(
                "$[?(@.line == 'Orange')].to.name", hasItems("Community College", "Haymarket")))
        .andDo(print());
  }
}
