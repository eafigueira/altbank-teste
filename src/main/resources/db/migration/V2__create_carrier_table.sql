CREATE TABLE carrier (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    default_carrier BOOLEAN NOT NULL
);
