package eu.ciechanowiec.templater.service;

import com.google.gson.Gson;
import eu.ciechanowiec.templater.model.HtmlData;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class JsonParser {

    @Value("${json.path}")
    private String jsonFilePath;

    @SneakyThrows
    HtmlData createHtmlData() {
        Gson gson = new Gson();

        Reader reader = Files.newBufferedReader(Paths.get(jsonFilePath));

        return gson.fromJson(reader, HtmlData.class);
    }
}
