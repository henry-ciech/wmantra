package eu.ciechanowiec.bot.service;

import com.google.gson.Gson;
import eu.ciechanowiec.bot.model.Location;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class LocationDetailsRetrieverTest {

    @Test
    void parseJsonCorrectly() {
        String json = "{\"city\":\"New York\",\"region\":\"New York\","
                + "\"country\":\"USA\",\"longitude\":-74.006,\"latitude\":40.7128}";
        Gson gson = new Gson();
        Location expectedLocation = gson.fromJson(json, Location.class);

        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        LocationRetriever locationRetriever = new LocationRetriever(restTemplate);

        ReflectionTestUtils.setField(locationRetriever, "apiUrl", "https://www.test.com/");

        ResponseEntity<String> responseEntity = new ResponseEntity<>(json, HttpStatus.OK);
        when(restTemplate.getForEntity(Mockito.anyString(), eq(String.class))).thenReturn(responseEntity);

        Location actualLocation = locationRetriever.retrieveLocationData(0, 0);

        assertEquals(expectedLocation, actualLocation);
    }
}
