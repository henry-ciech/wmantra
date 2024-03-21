package eu.ciechanowiec.templater.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class WeatherApiClient {

    @Value("${api.key}")
    private String apiKey;
    @Value("${api.url}")
    private String url;

    String getResponse(double latitude, double longitude) {
        log.info("Send request to the api. latitude: {}; longitude: {}", latitude, longitude);
        RestOperations restTemplate = new RestTemplate();
        String urlWithLocation = fillWeatherUrl(latitude, longitude);
        ResponseEntity<String> response = restTemplate.getForEntity(urlWithLocation, String.class);
        return response.getBody();
    }

    @SuppressWarnings({"SameParameterValue", "MagicNumber", "RedundantSuppression"})
    private String fillWeatherUrl(double latitude, double longitude) {
        UriComponentsBuilder currentBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("q", latitude + "," + longitude)
                .queryParam("days", 3);
        log.info("link requesting to: {}", currentBuilder);
        return currentBuilder.toUriString();
    }
}
