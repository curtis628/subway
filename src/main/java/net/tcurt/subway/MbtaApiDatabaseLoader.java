package net.tcurt.subway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tcurt.subway.entity.Line;
import net.tcurt.subway.entity.Stop;
import net.tcurt.subway.service.SubwayService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MbtaApiDatabaseLoader {
  private static final Pattern IS_NUMBER = Pattern.compile("\\d+");

  private final SubwayService subwayService;
  private final RestTemplate restTemplate;

  public void loadDatabase() {
    if (subwayService.getStops().isEmpty()) {
      log.info("Initializing DB via MBTA API...");
    } else {
      log.info("Skip DB initialization as it already has data in it.");
      return;
    }

    Map<Stop, Set<String>> parentToChildStopsMap = new HashMap<>();
    Map<Line, ArrayNode> lineToStopsData = new HashMap<>();

    log.info("Retrieving routes from MBTA...");
    JsonNode routes = restTemplate.getForObject("/routes?filter[type]=0,1", JsonNode.class);
    JsonNode routesData = routes.get("data");
    for (JsonNode route : routesData) {
      String name = route.get("id").asText();
      log.debug("Processing MBTA route={}", name);
      Line line = new Line(name);
      subwayService.createLine(line);

      log.debug("Retrieving stops from MBTA with route={}...", name);
      JsonNode stops =
          restTemplate.getForObject(
              "/stops?filter[route]={route}&include=child_stops", JsonNode.class, name);
      ArrayNode stopsData = (ArrayNode) stops.get("data");
      lineToStopsData.put(line, stopsData);
      log.info("Found {} stops for route={}", stopsData.size(), name);
      for (JsonNode stop : stopsData) {
        JsonNode attributes = stop.get("attributes");
        double latitude = attributes.get("latitude").asDouble();
        double longitude = attributes.get("longitude").asDouble();
        String stopId = stop.get("id").asText();
        String stopName = attributes.get("name").asText();
        Stop myStop =
            Stop.builder()
                .id(stopId)
                .name(stopName)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        subwayService.getOrCreateStop(myStop);
      }
    }

    for (Line line : subwayService.getLines()) {
      ArrayNode stopsData = lineToStopsData.get(line);
      for (JsonNode stop : stopsData) {
        String stopId = stop.get("id").asText();
        Stop myStop = subwayService.getStop(stopId);
        ArrayNode childStops = (ArrayNode) stop.get("relationships").get("child_stops").get("data");
        Set<String> childStopIds =
            StreamSupport.stream(childStops.spliterator(), false)
                .map(node -> node.get("id").asText())
                .filter(childStopId -> IS_NUMBER.matcher(childStopId).matches())
                .collect(Collectors.toSet());
        log.debug("{} has {} child stops", myStop, childStopIds.size());
        parentToChildStopsMap.put(myStop, childStopIds);
      }

      JsonNode trips =
          restTemplate.getForObject(
              "/trips?include=stops&filter[route]={route}", JsonNode.class, line.getName());
      JsonNode tripsData = trips.get("data");
      for (JsonNode trip : tripsData) {
        String id = trip.get("id").asText();
        JsonNode relationships = trip.get("relationships");
        String routePattern = relationships.get("route_pattern").get("data").get("id").asText();
        ArrayNode tripStops = (ArrayNode) relationships.get("stops").get("data");
        int stopNum = tripStops.size();
        if (stopNum == 0) {
          continue;
        }

        // MBTA returns trips don't match the 'filter[route]' given... so manually filter ourselves.
        String tripRouteId = relationships.get("route").get("data").get("id").asText();
        if (!line.getName().equals(tripRouteId)) {
          log.debug("Filtering out trip={} as it doesn't match {}", id, line);
          continue;
        }

        log.debug(
            "Processing MBTA trip={} route_pattern={} with {} stops", id, routePattern, stopNum);
        for (int i = 0; i < stopNum; i++) {
          JsonNode currentStopNode = tripStops.get(i);
          String currentStopId = currentStopNode.get("id").asText();
          Stop current = findParentStop(parentToChildStopsMap, currentStopId);

          if (i - 1 >= 0) {
            JsonNode prevStopNode = tripStops.get(i - 1);
            String prevStopId = prevStopNode.get("id").asText();
            Stop prev = findParentStop(parentToChildStopsMap, prevStopId);
            subwayService.getOrCreateConnection(current, prev, line);
          }
          if (i + 1 < stopNum) {
            JsonNode nextStopNode = tripStops.get(i + 1);
            String nextStopId = nextStopNode.get("id").asText();
            Stop next = findParentStop(parentToChildStopsMap, nextStopId);
            subwayService.getOrCreateConnection(current, next, line);
          }
        }
      }
    }
  }

  private Stop findParentStop(Map<Stop, Set<String>> parentToChildStopsMap, String childStopId) {
    for (Map.Entry<Stop, Set<String>> entry : parentToChildStopsMap.entrySet()) {
      if (entry.getValue().contains(childStopId)) {
        Stop parent = entry.getKey();
        log.debug("childStopId={} is associated with parent {}", childStopId, parent);
        return parent;
      }
    }
    log.warn("No parent stop found for childStopId={}... looking up directly", childStopId);
    JsonNode stop = restTemplate.getForObject("/stops/{id}", JsonNode.class, childStopId);
    JsonNode relationships = stop.get("data").get("relationships");
    JsonNode parentStation = relationships.get("parent_station");
    String parentStationId = parentStation.get("data").get("id").asText();
    Stop parent = subwayService.getStop(parentStationId);

    if (parent == null) {
      log.warn("Failed to find parent={} stop in DB", parentStationId);
    } else {
      Set<String> childStops = parentToChildStopsMap.getOrDefault(parent, new HashSet<>());
      childStops.add(childStopId);
      parentToChildStopsMap.put(parent, childStops);
    }

    return parent;
  }
}
