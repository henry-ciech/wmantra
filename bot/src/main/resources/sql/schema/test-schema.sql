DROP TABLE IF EXISTS user_data;

CREATE TABLE IF NOT EXISTS USER_DATA
(
    chatid BIGINT NOT NULL,
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION,
    time TIME WITHOUT TIME ZONE,
    user_id VARCHAR(255),
    user_name VARCHAR(255),
    PRIMARY KEY (chatid)
    );
