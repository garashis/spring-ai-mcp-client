DROP TABLE REVIEW;

CREATE TABLE IF NOT EXISTS REVIEW (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(255),
                        review TEXT,
                        sentiment tinyInt,
                        response TEXT
);
