package eu.ciechanowiec.bot.utils;

import eu.ciechanowiec.bot.repository.UserPaginationRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import eu.ciechanowiec.bot.model.User;
import org.springframework.stereotype.Service;

import java.util.stream.StreamSupport;

@Service
class DatabaseTraverser {

    private final UserPaginationRepository userPaginationRepository;
    private final ReportsScheduler reportsScheduler;

    @Autowired
    DatabaseTraverser(UserPaginationRepository userPaginationRepository, ReportsScheduler reportsScheduler) {
        this.userPaginationRepository = userPaginationRepository;
        this.reportsScheduler = reportsScheduler;
    }

    @SneakyThrows
    @EventListener(ContextRefreshedEvent.class)
    private void scheduleDatabase() {
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
        StreamSupport.stream(users.spliterator(), false)
                .filter(user -> user.getTime() != null && user.getLongitude() != null && user.getLatitude() != null)
                .forEach(reportsScheduler::schedule);
    }
}
