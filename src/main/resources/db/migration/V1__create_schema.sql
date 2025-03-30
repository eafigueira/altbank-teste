CREATE TABLE customers
(
    id BINARY (16)  PRIMARY KEY NOT NULL,
    name            VARCHAR(100) NOT NULL,
    document_number VARCHAR(20)  NOT NULL,
    status          VARCHAR(20) NOT NULL,
    email           VARCHAR(100),
    created_at      TIMESTAMP    NULL,
    updated_at      TIMESTAMP    NULL,
    street          VARCHAR(150) NOT NULL,
    number          VARCHAR(50)  NULL,
    complement      VARCHAR(50)  NULL,
    neighborhood    VARCHAR(50)  NOT NULL,
    city            VARCHAR(50)  NOT NULL,
    state           VARCHAR(30)  NOT NULL,
    zipCode         VARCHAR(10)  NOT NULL
);

CREATE INDEX idx_customer_status ON customers(status);
CREATE INDEX idx_customer_document_number ON customers(document_number);

CREATE TABLE accounts
(
    id BINARY (16)          PRIMARY KEY NOT NULL,
    customer_id             BINARY (16) NOT NULL,
    created_at              TIMESTAMP   NULL,
    updated_at              TIMESTAMP   NULL,
    status                  VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE INDEX idx_account_status ON accounts(status);

CREATE TABLE carriers
(
    id              BINARY (16)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    document_number VARCHAR(14)  NOT NULL,
    client_id       VARCHAR(255) NOT NULL,
    client_secret   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP    NULL,
    updated_at      TIMESTAMP    NULL,
    status          VARCHAR(20)  NOT NULL
);

CREATE INDEX idx_carrier_status ON carriers(status);

CREATE INDEX idx_carrier_document_number ON carriers(document_number);

CREATE TABLE processors
(
    id              BINARY (16)  PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    client_id       VARCHAR(255) NOT NULL UNIQUE,
    client_secret   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP    NULL,
    updated_at      TIMESTAMP    NULL
);

CREATE TABLE cards
(
    id                     BINARY (16)  PRIMARY KEY,
    account_id             BINARY (16)  NOT NULL,
    type                   VARCHAR(20)  NOT NULL,
    number                 VARCHAR(30),
    cvv                    VARCHAR(10),
    cvv_expiration         TIMESTAMP,
    status                 VARCHAR(20)  NOT NULL,
    created_at             TIMESTAMP    NULL,
    updated_at             TIMESTAMP    NULL,

    FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_card_status ON cards(status);
CREATE INDEX idx_cad_number ON cards(number);

CREATE TABLE card_delivery_requests (
    id                  BINARY(16)      PRIMARY KEY,
    card_id             BINARY (16)     NOT NULL,
    carrier_id          BINARY (16)     NOT NULL,
    tracking_code       VARCHAR(255),
    delivery_status     VARCHAR(20),
    delivery_address       TEXT,
    delivery_return_reason TEXT,
    delivered_at        TIMESTAMP       NULL,
    created_at          TIMESTAMP       NULL,
    updated_at          TIMESTAMP       NULL,

    CONSTRAINT fk_card FOREIGN KEY (card_id) REFERENCES cards(id),
    CONSTRAINT fk_carrier FOREIGN KEY (carrier_id) REFERENCES carriers(id)
);

CREATE INDEX idx_card_delivery_tracking_code ON card_delivery_requests(tracking_code);
CREATE INDEX idx_card_delivery_delivery_status ON card_delivery_requests(delivery_status);
CREATE INDEX idx_card_delivery_created_at ON card_delivery_requests(created_at);

CREATE TABLE card_processors (
    id              BINARY(16)      PRIMARY KEY,
    card_id         BINARY(16)      NOT NULL,
    processor_id    BINARY(16)      NOT NULL,
    created_at      TIMESTAMP       NULL,
    updated_at      TIMESTAMP       NULL,

    CONSTRAINT fk_card_processors_card FOREIGN KEY (card_id) REFERENCES cards(id),
    CONSTRAINT fk_processor_card FOREIGN KEY (processor_id) REFERENCES processors(id)
);

CREATE INDEX idx_card_processor_created_at ON card_processors(created_at);