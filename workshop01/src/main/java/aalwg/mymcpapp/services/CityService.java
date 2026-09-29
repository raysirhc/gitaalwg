package aalwg.mymcpapp.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class CityService {

  private static final String URL = "https://api.open-meteo.com/";

  @Value("classpath:/data/cities_latlng.csv")
  private Resource resource;

  private Map<String, City> cities = new HashMap<>();
  private final RestClient client;

  public CityService(RestClient.Builder builder) {
    client = builder.baseUrl(URL)
      .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
      .build();
  }

  @PostConstruct
  private void init() throws Exception {
    //id;country;city;latitude;longitude;altitude 
    try (InputStreamReader isr = new InputStreamReader(resource.getInputStream())) {
      BufferedReader br = new BufferedReader(isr);
      br.lines().skip(1)
        .map(line -> line.split(";"))
        .map(terms -> {
          var country = terms[1];
          var city = terms[2];
          var lat = Float.parseFloat(terms[3]);
          var lng = Float.parseFloat(terms[4]);
          var alt = Float.parseFloat(terms[5]);
          return (new City(country, city, lat, lng, alt)).toMapEntry();
        })
        .forEach(v -> {
          cities.put(v.getKey(), v.getValue());
        });
    }

  }

  public Optional<City> getLatLngAlt(String city) {
    var key = City.key(city);
    return Optional.ofNullable(cities.get(key));
  }

  public Optional<JsonObject> getWeather(float latitude, float longitude) {
    try {
      String payload = client.get()
        .uri(uriBuilder -> uriBuilder.path("/v1/forecast")
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("current", "temperature_2m")
            .build())
        .retrieve().body(String.class) ;
      try (Reader r = new StringReader(payload)) {
        JsonReader jsonReader = Json.createReader(r);
        JsonObject result = jsonReader.readObject();
        return Optional.of(
            Json.createObjectBuilder()
            .add("unit", result.getJsonObject("current_units").getString("temperature_2m"))
            .add("temperature", result.getJsonObject("current").getJsonNumber("temperature_2m").doubleValue())
            .add("time", result.getJsonObject("current").getString("time"))
            .build()
        );
      }
    } catch (Exception ex) {
      return Optional.empty();
    }
  }

  public Optional<JsonObject> getWeather(City city) {
    return getWeather(city.latitude(), city.longitude());
  }
}
