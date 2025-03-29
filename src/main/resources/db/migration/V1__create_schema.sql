CREATE TABLE customers
(
    id BINARY (16) PRIMARY KEY NOT NULL,
    name            VARCHAR(100) NOT NULL,
    document_number VARCHAR(20)  NOT NULL,
    email           VARCHAR(100),
    created_at      TIMESTAMP    NULL,
    updated_at      TIMESTAMP    NULL,

    street          VARCHAR(150) NOT NULL,
    number          VARCHAR(50)  NULL,
    complement      VARCHAR(50)  NULL,
    neighborhood    VARCHAR(50)  NOT NULL,
    city            VARCHAR(50)  NOT NULL,
    state           VARCHAR(30)  NOT NULL,
    zipCode         VARCHAR(10)  NOT NULL,
    status          VARCHAR(20)  NOT NULL
);

CREATE TABLE accounts
(
    id BINARY (16) PRIMARY KEY NOT NULL,
    customer_id BINARY (16) NOT NULL,
    status     VARCHAR(20) NOT NULL,
    created_at TIMESTAMP   NULL,
    updated_at TIMESTAMP   NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE cards
(
    id BINARY (16) PRIMARY KEY,
    account_id BINARY (16) NOT NULL,
    type                   VARCHAR(20) NOT NULL,
    number                 VARCHAR(30),
    cvv                    INT,
    cvv_expiration         TIMESTAMP,
    status                 VARCHAR(20) NOT NULL,
    delivery_tracking_id   VARCHAR(100),
    delivery_status        VARCHAR(50),
    delivery_date          TIMESTAMP,
    delivery_return_reason VARCHAR(255),
    created_at             TIMESTAMP   NULL,
    updated_at             TIMESTAMP   NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE TABLE carriers
(
    id BINARY (16) PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    client_id       VARCHAR(255) NOT NULL UNIQUE,
    client_secret   VARCHAR(255) NOT NULL,
    default_carrier BOOLEAN      NOT NULL,
    created_at      TIMESTAMP    NULL,
    updated_at      TIMESTAMP    NULL,
    status          VARCHAR(20)  NOT NULL
);