package net.tcurt.subway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class SubwayApplication {

  public static void main(String[] args) {
    SpringApplication.run(SubwayApplication.class, args);
  }
}
