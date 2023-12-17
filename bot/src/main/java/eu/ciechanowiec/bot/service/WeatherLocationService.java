package eu.ciechanowiec.bot.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ciechanowiec.bot.model.LocationData;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class WeatherLocationService {

    private final RestTemplate restTemplate;
    private static final String LOCATION = "location";

    public WeatherLocationService() {
        this.restTemplate = new RestTemplate();
    }

    @SneakyThrows
    public LocationData getLocationData(double latitude, double longitude) {
        String url = "http://api.weatherapi.com/v1/current.json";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", "2bcfc05428d041a88e683012231211")
                .queryParam("q", latitude + "," + longitude);

        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode cityNode = rootNode.path(LOCATION).path("name");
        JsonNode regionNode = rootNode.path(LOCATION).path("region");
        JsonNode countryNode = rootNode.path(LOCATION).path("country");
        String cityResponse = cityNode.asText();
        String countryResponse = countryNode.asText();
        String regionResponse = regionNode.asText();
        return new LocationData(cityResponse, regionResponse, countryResponse, longitude, latitude);
    }
}
