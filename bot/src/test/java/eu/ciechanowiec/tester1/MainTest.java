package eu.ciechanowiec.tester1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration
class MainTest {

    @Test
    void contextLoads() {
        assertTrue(true);
    }
}
