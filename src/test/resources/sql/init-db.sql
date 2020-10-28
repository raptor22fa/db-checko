CREATE TABLE user_
(
    id   bigint                NOT NULL,
    name character varying(50) NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id)
);

INSERT INTO user_
VALUES (1, 'Raptor');
INSERT INTO user_
VALUES (2, 'Peter');
INSERT INTO user_
VALUES (3, 'John');
