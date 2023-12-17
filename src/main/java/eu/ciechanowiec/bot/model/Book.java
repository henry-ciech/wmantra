package eu.ciechanowiec.bot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "books")
@SuppressWarnings("JpaDataSourceORMInspection")
public class Book {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SuppressWarnings({"unused", "InstanceVariableMayNotBeInitialized"})
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "rating")
    @Setter
    private Integer rating;

    @Column(name = "description")
    private String description;

    public Book(String title, String author, Integer rating, String description) {
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.description = description;
    }
}
