package eu.ciechanowiec.bot.repository;

import eu.ciechanowiec.bot.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public interface UserRepository extends CrudRepository<User, Long> {

    @Query(value = "SELECT * FROM users.user_data WHERE chatId = ?1 LIMIT 1", nativeQuery = true)
    User findUser(long chatId);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM users.user_data WHERE chatId = ?1"
            + " AND time IS NOT NULL", nativeQuery = true)
    boolean isTimeSpecified(long chatId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO users.user_data (chatId, user_id, user_name) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void createUserWithChatIdAndUserInfo(long chatId, String userId, String userName);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM users.user_data WHERE chatId = ?1 "
            + "AND latitude IS NOT NULL AND longitude IS NOT NULL", nativeQuery = true)
    boolean isLocationSpecified(long chatId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users.user_data SET latitude = ?2, longitude = ?3 WHERE chatId = ?1", nativeQuery = true)
    void updateLocation(long chatId, double latitude, double longitude);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM users.user_data WHERE chatId = ?1",
            nativeQuery = true)
    boolean isUserExists(long chatId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users.user_data SET time = ?2 WHERE chatId = ?1", nativeQuery = true)
    void updateTime(long chatId, LocalTime time);

    @Query(value = "SELECT latitude FROM users.user_data WHERE chatId = ?1", nativeQuery = true)
    Double findLatitude(long chatId);

    @Query(value = "SELECT longitude FROM users.user_data WHERE chatId = ?1", nativeQuery = true)
    Double findLongitude(long chatId);

    @Override
    @NonNull
    <S extends User> S save(@NonNull S entity);
}
