package eu.ciechanowiec.bot.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import eu.ciechanowiec.bot.repository.UserPaginationRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
@SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD", justification = "called automatically")
@Slf4j
class DatabaseTraverser {

    private static final int PAGE_SIZE = 100;
    private final UserPaginationRepository userPaginationRepository;
    private final ReportsScheduler reportsScheduler;

    @Autowired
    DatabaseTraverser(UserPaginationRepository userPaginationRepository, ReportsScheduler reportsScheduler) {
        this.userPaginationRepository = userPaginationRepository;
        this.reportsScheduler = reportsScheduler;
    }

    @SneakyThrows
    @EventListener(ContextRefreshedEvent.class)
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void scheduleDatabase() {
        log.info("Schedule database");
        int page = 0;
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<User> userPage;

        do {
            userPage = userPaginationRepository.findAll(pageable);
            processUsers(userPage.getContent());
            pageable = userPage.nextPageable();
        } while (userPage.hasNext());
    }

    @SneakyThrows
    @SuppressWarnings("Regexp")
    private void processUsers(Iterable<User> users) {
        StreamSupport.stream(users.spliterator(), false)
                .filter(user -> user.getTime() != null && user.getLongitude() != null && user.getLatitude() != null)
                .forEach(reportsScheduler::schedule);
    }
}
