package net.tcurt.subway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Slf4j
public class SubwayApplication {

  @Value("${MBTA_API_KEY:}")
  private String mbtaApiKey;

  public static void main(String[] args) {
    SpringApplication.run(SubwayApplication.class, args);
  }

  @Bean
  public RestTemplate mbtaClient(RestTemplateBuilder builder) {
    builder = builder.rootUri("https://api-v3.mbta.com");
    if (mbtaApiKey != null && !mbtaApiKey.isEmpty()) {
      log.info("Adding in 'x-api-key': ****{}", mbtaApiKey.substring(mbtaApiKey.length() - 5));
      builder = builder.defaultHeader("x-api-key", mbtaApiKey);
    }
    return builder.build();
  }

  @Bean
  @Profile("!test")
  public CommandLineRunner init(MbtaApiDatabaseLoader loader) {
    return (args) -> {
      loader.loadDatabase();
    };
  }
}
