package eu.ciechanowiec.bot.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Service
public class ScreenshoterClient {

    @Value("${base.url}")
    private String baseUrl;

    @SneakyThrows
    public ByteArrayInputStream getImageAsInputStream(double longitude, double latitude,
                                                      ByteArrayInputStream errorPhoto) {
        String url = String.format("%s/take-screenshot?longitude=%f&latitude=%f", baseUrl, longitude, latitude);
        RestOperations restTemplate = new RestTemplate();

        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        Optional<byte[]> imageBytesNullable = Optional.ofNullable(response.getBody());
        return imageBytesNullable.map(ByteArrayInputStream::new).orElse(errorPhoto);
    }
}
