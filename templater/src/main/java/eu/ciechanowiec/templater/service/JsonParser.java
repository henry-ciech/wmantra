package eu.ciechanowiec.templater.service;

import com.google.gson.Gson;
import eu.ciechanowiec.templater.model.HtmlData;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Component
public class JsonParser {

    @Value("${json.path}")
    private String jsonFilePath;

    @SneakyThrows
    @SuppressWarnings("PMD.CloseResource")
    HtmlData createHtmlData() {
        Gson gson = new Gson();

        InputStreamSource classPathResource = new ClassPathResource(jsonFilePath);
        try (InputStream inputStream = classPathResource.getInputStream();
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            return gson.fromJson(reader, HtmlData.class);
        }
    }
}
