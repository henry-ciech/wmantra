CREATE SCHEMA IF NOT EXISTS users;

DROP TABLE IF EXISTS users.user_data;

CREATE TABLE IF NOT EXISTS users.user_data
(
    chatid BIGINT NOT NULL,
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION,
    time TIME WITHOUT TIME ZONE,
    user_id VARCHAR(255),
    user_name VARCHAR(255),
    PRIMARY KEY (chatid)
    );
