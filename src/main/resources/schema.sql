CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    balance DOUBLE PRECISION,
    role VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS vouchers (
                                        id UUID PRIMARY KEY,
                                        title VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    tour_type VARCHAR(50),
    transfer_type VARCHAR(50),
    hotel_type VARCHAR(50),
    status VARCHAR(50),
    arrival_date DATE NOT NULL,
    eviction_date DATE NOT NULL,
    is_hot BOOLEAN NOT NULL DEFAULT FALSE,
    user_id UUID,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
    );