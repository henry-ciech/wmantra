package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.repository.UserPaginationRepository;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import eu.ciechanowiec.bot.model.User;
import org.springframework.stereotype.Component;

@Component
class DatabaseTraverser {

    private final UserPaginationRepository userPaginationRepository;
    private final WeatherScheduler weatherScheduler;

    @Autowired
    DatabaseTraverser(UserPaginationRepository userPaginationRepository, WeatherScheduler weatherScheduler) {
        this.userPaginationRepository = userPaginationRepository;
        this.weatherScheduler = weatherScheduler;
    }


    @SneakyThrows
    @PostConstruct
    public void scheduleDatabase() {
        int page = 0;
        int size = 100;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;

        do {
            userPage = userPaginationRepository.findAll(pageable);
            processUsers(userPage.getContent());
            pageable = userPage.nextPageable();
        } while (userPage.hasNext());
    }

    @SneakyThrows
    private void processUsers(Iterable<User> users) {
        for (User user : users) {
            if (user.getTime() != null && user.getLongitude() != null && user.getLatitude() != null) {
                weatherScheduler.schedule(user);
            }
        }
    }
}
