package aalwg.mymcpapp.mcp;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import aalwg.mymcpapp.services.City;
import aalwg.mymcpapp.services.CityService;
import jakarta.json.JsonObject;

@Component
public class CityTools {

  private static final Logger logger = LoggerFactory.getLogger(CityTools.class);

  @Autowired
  private CityService citySvc;

  @McpTool(
    name = "get_latlngalt",
    description = """
      Get the latitude, longitude and altitude of a city as a JSON object.
      The respoonse JSON object contains the following attributes:
      - key: an identifier 
      - country: country the city is in
      - city: city's name
      - latitude: latitude of the city in float
      - longitude: longitude of the city in float
      - altitude: altitude of the city in float
      This is an example of a valid result for the city of Tokyo
         {"key":"tokyo","country":"Japan","city":"Tokyo","latitude":35.68952560424805,"longitude":139.69168090820312,"altitude":40.0}
      Returns an empty JSON object {} if no geographic coordinates are found for the city.
    """
  )
  public String getLatLngAlt(
      @McpToolParam(description = "city name") String city) {

    logger.info("Getting geographic coordinates for city=%s".formatted(city));

    Optional<City> opt = citySvc.getLatLngAlt(city);
    if (opt.isEmpty())
      return "{}";
    return opt.get().toJSON().toString();
  }

  @McpTool(
    name = "get_weather",
    description = """
      Get the weather at the city give by its latitude and longitude. 
      The respoonse JSON object contains the following attributes:
      - unit: temperature's unit
      - temperature: temperature reading
      - time: the time when the temperature was taken
      Returns an empty JSON object {} if 
      Returns null if no weather details is available.
    """
  )
  public String getWeather(
      @McpToolParam(description = "city's latitude") float latitude, 
      @McpToolParam(description = "city's longitude") float longitude) {

    logger.info("Getting weather details for lat=%f, lng=%f".formatted(latitude, longitude));

    Optional<JsonObject> opt = citySvc.getWeather(latitude, longitude);
    if (opt.isEmpty())
      return "{}";
    return opt.get().toString();
  }

}
