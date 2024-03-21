CREATE SCHEMA IF NOT EXISTS users;
CREATE TABLE IF NOT EXISTS users.user_data
(
    chatid bigint NOT NULL,
    longitude double precision,
    latitude double precision,
    "time" time without time zone,
    user_id character varying(255) COLLATE pg_catalog."default",
    user_name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT user_data_pkey PRIMARY KEY (chatid)
    )
