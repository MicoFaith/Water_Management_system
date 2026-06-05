CREATE TABLE otp_verifications (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expiry_time TIMESTAMP NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_otp_email ON otp_verifications (email);
