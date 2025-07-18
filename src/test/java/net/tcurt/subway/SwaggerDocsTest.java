package net.tcurt.subway;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SwaggerDocsTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void openApiDocs_shouldContainStopEndpoint() throws Exception {
    mockMvc
        .perform(get("/api-docs"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.paths['/api/v1/stops']").exists())
        .andExpect(jsonPath("$.paths['/api/v1/stops'].get.summary").value("Query all stops"));
  }

  @Test
  void swaggerUi_shouldBeAccessible() throws Exception {
    mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
  }
}
