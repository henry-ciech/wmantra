package eu.ciechanowiec.bot.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Service
@Slf4j
public class ScreenshotterClient {

    @Value("${base.url}")
    private String baseUrl;

    @SneakyThrows
    Optional<ByteArrayInputStream> getImageAsInputStream(double longitude, double latitude) {
        log.info("Send request to the screenshotter");
        String url = String.format("%s/take-screenshot?longitude=%f&latitude=%f", baseUrl, longitude, latitude);
        log.info("Screenshotter url requesting to {}", url);

        RestOperations restTemplate = new RestTemplate();

        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            Optional<byte[]> imageBytesNullable = Optional.ofNullable(response.getBody());
            return imageBytesNullable.map(ByteArrayInputStream::new);
        } catch (HttpClientErrorException ex) {
            log.error("Error taking screenshot: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
