DROP TABLE IF EXISTS books;

CREATE TABLE IF NOT EXISTS books
(
    id          INT            NOT NULL AUTO_INCREMENT,
    title       VARCHAR(100)   NOT NULL,
    author      VARCHAR(100)   NOT NULL,
    rating      INT            NOT NULL,
    description VARCHAR(1000)  NOT NULL,
    PRIMARY KEY (id)
);
