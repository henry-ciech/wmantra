package eu.ciechanowiec.templater.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WeatherApiClient {


    @Value("${api.key}")
    private String apiKey;
    @Value("${api.url}")
    private String url;

    String getResponse(double latitude, double longitude) {
        RestOperations restTemplate = new RestTemplate();
        String urlWithLocation = fillWeatherUrl(latitude, longitude);
        ResponseEntity<String> response = restTemplate.getForEntity(urlWithLocation, String.class);
        return response.getBody();
    }

    @SuppressWarnings("SameParameterValue")
    private String fillWeatherUrl(double latitude, double longitude) {
        UriComponentsBuilder currentBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("key", apiKey)
                .queryParam("q", latitude + "," + longitude)
                .queryParam("days", 3);

        return currentBuilder.toUriString();
    }
}
