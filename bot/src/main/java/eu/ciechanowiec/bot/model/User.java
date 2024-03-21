package eu.ciechanowiec.bot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@SuppressWarnings("JpaDataSourceORMInspection")
@Getter
@Entity
@Table(name = "user_data", schema = "users")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @Column(name = "chatid")
    private long chatId;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "time", columnDefinition = "TIME WITHOUT TIME ZONE")
    private LocalTime time;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Transient
    @Getter(AccessLevel.NONE)
    private boolean isTimeAdjusted;

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    public User(User user, LocalTime time) {
        this(user.chatId, user.longitude, user.latitude, time,
                user.userId, user.userName, true);
    }
}
