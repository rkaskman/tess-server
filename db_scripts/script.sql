CREATE USER faust WITH PASSWORD 'faustf';
CREATE DATABASE financemanager;
GRANT ALL PRIVILEGES ON DATABASE financemanager TO faust;


CREATE SEQUENCE user_id_seq;

CREATE TABLE user
(
  user_id    NUMERIC(10, 0) NOT NULL DEFAULT nextval('user_id_seq'),
  google_id text,
  CONSTRAINT user_pk PRIMARY KEY (user_id)
);