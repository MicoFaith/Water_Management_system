CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_names VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    full_names VARCHAR(255) NOT NULL,
    national_id VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    address VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id BIGINT UNIQUE REFERENCES users(id)
);

CREATE TABLE meters (
    id BIGSERIAL PRIMARY KEY,
    meter_number VARCHAR(50) NOT NULL UNIQUE,
    meter_type VARCHAR(20) NOT NULL,
    installation_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id)
);

CREATE TABLE meter_readings (
    id BIGSERIAL PRIMARY KEY,
    meter_id BIGINT NOT NULL REFERENCES meters(id),
    previous_reading NUMERIC(12, 2) NOT NULL,
    current_reading NUMERIC(12, 2) NOT NULL,
    reading_date DATE NOT NULL,
    billing_month INTEGER NOT NULL,
    billing_year INTEGER NOT NULL,
    UNIQUE (meter_id, billing_month, billing_year)
);

CREATE TABLE tariffs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    meter_type VARCHAR(20) NOT NULL,
    tariff_type VARCHAR(20) NOT NULL,
    flat_rate NUMERIC(12, 2),
    version INTEGER NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    active BOOLEAN NOT NULL
);

CREATE TABLE tariff_tiers (
    id BIGSERIAL PRIMARY KEY,
    tariff_id BIGINT NOT NULL REFERENCES tariffs(id) ON DELETE CASCADE,
    min_consumption NUMERIC(12, 2) NOT NULL,
    max_consumption NUMERIC(12, 2),
    rate_per_unit NUMERIC(12, 2) NOT NULL
);

CREATE TABLE service_charges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    meter_type VARCHAR(20) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    version INTEGER NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    active BOOLEAN NOT NULL
);

CREATE TABLE taxes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    percentage NUMERIC(5, 2) NOT NULL,
    version INTEGER NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    active BOOLEAN NOT NULL
);

CREATE TABLE penalties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    percentage NUMERIC(5, 2) NOT NULL,
    days_after_due INTEGER NOT NULL,
    version INTEGER NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    active BOOLEAN NOT NULL
);

CREATE TABLE bills (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    meter_type VARCHAR(20) NOT NULL,
    billing_month INTEGER NOT NULL,
    billing_year INTEGER NOT NULL,
    consumption_amount NUMERIC(12, 2) NOT NULL,
    service_charge_amount NUMERIC(12, 2) NOT NULL,
    tax_amount NUMERIC(12, 2) NOT NULL,
    penalty_amount NUMERIC(12, 2) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,
    amount_paid NUMERIC(12, 2) NOT NULL,
    outstanding_balance NUMERIC(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    due_date DATE NOT NULL,
    generated_date DATE NOT NULL,
    UNIQUE (customer_id, meter_type, billing_month, billing_year)
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES bills(id),
    amount_paid NUMERIC(12, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_date DATE NOT NULL
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    bill_id BIGINT REFERENCES bills(id),
    message TEXT NOT NULL,
    notification_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
