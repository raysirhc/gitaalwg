package aalwg.mymcpapp.services;

import java.util.AbstractMap;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObject;

//id;country;city;latitude;longitude;altitude 
public record City(String country, String city, float latitude, float longitude, float altitude) { 

  public static String key(String city) {
    return city.replaceAll(" ", "").toLowerCase();
  }

  public Map.Entry<String, City> toMapEntry() {
    return new AbstractMap.SimpleImmutableEntry<>(key(city()), this);
  }

  public JsonObject toJSON() {
    return Json.createObjectBuilder()
      .add("key", key(city()))
      .add("country", country())
      .add("city", city())
      .add("latitude", latitude())
      .add("longitude", longitude())
      .add("altitude", altitude())
      .build();
  }

  @Override
  public String toString() {
    return "country=%s, city=%s, lat=%f, lng=%f, alt=%f".formatted(country(), city(), latitude(), longitude(), altitude());
  }
}
