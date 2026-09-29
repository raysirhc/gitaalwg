package aalwg.mymcpapp.controllers;

import java.util.Date;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.json.Json;
import jakarta.json.JsonArray;

@Controller
@RequestMapping
public class ShowVersionController {

  @GetMapping(path={"/", "/index.html"}, produces = MediaType.TEXT_HTML_VALUE)
  public ModelAndView getIndex() {
    final ModelAndView mav = new ModelAndView("index");
    mav.addObject("versions", Constants.VERSIONS);
    mav.addObject("timestamp", (new Date()).toString());
    mav.setStatus(HttpStatusCode.valueOf(200));
    return mav;
  }

  @GetMapping(path="/api/versions", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> getAsJSON() {
    JsonArray versions = Json.createArrayBuilder(Constants.VERSIONS).build();
    return ResponseEntity.ok(
        Json.createObjectBuilder()
        .add("versions", versions)
        .add("timestamp", (new Date()).toString())
        .build().toString());
  }
}
