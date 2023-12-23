package eu.ciechanowiec.bot.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ciechanowiec.bot.model.LocationData;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LocationRetriever {

    private static final String LOCATION_JSON_PATH = "location";

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.url}")
    private String apiUrl;

    @SneakyThrows
    public LocationData retrieveLocationData(double latitude, double longitude) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .queryParam("q", latitude + "," + longitude);

        RestOperations restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode cityNode = rootNode.path(LOCATION_JSON_PATH).path("name");
        JsonNode regionNode = rootNode.path(LOCATION_JSON_PATH).path("region");
        JsonNode countryNode = rootNode.path(LOCATION_JSON_PATH).path("country");
        String cityResponse = cityNode.asText();
        String countryResponse = countryNode.asText();
        String regionResponse = regionNode.asText();
        return new LocationData(cityResponse, regionResponse, countryResponse, longitude, latitude);
    }
}
