CREATE TABLE customer (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    created_at DATETIME NOT NULL
);

CREATE TABLE account (
    id BINARY(16) PRIMARY KEY,
    customer_id BINARY(16) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE card (
    id BINARY(16) PRIMARY KEY,
    account_id BINARY(16) NOT NULL,
    type VARCHAR(20) NOT NULL,
    number VARCHAR(30),
    cvv INT,
    cvv_expiration DATETIME,
    status VARCHAR(20) NOT NULL,
    delivery_tracking_id VARCHAR(100),
    delivery_status VARCHAR(50),
    delivery_date DATETIME,
    delivery_return_reason VARCHAR(255),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id)
);
