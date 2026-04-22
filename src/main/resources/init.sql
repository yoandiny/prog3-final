-- Create Enums
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE frequency AS ENUM ('WEEKLY', 'MONTHLY', 'ANNUALLY', 'PUNCTUALLY');
CREATE TYPE activity_status AS ENUM ('ACTIVE', 'INACTIVE');
CREATE TYPE payment_mode AS ENUM ('CASH', 'MOBILE_BANKING', 'BANK_TRANSFER');
CREATE TYPE mobile_service_name AS ENUM ('ORANGE_MONEY', 'MVOLA', 'AIRTEL_MONEY');
CREATE TYPE bank_name AS ENUM ('BRED', 'MCB', 'BMOI', 'BOA', 'BGFI', 'AFG', 'ACCES_BAQUE', 'BAOBAB', 'SIPEM');
CREATE TYPE member_occupation AS ENUM ('JUNIOR', 'SENIOR', 'SECRETARY', 'TREASURER', 'VICE_PRESIDENT', 'PRESIDENT');

-- Tables
CREATE TABLE collectivity (
    id VARCHAR(50) PRIMARY KEY,
    number INTEGER UNIQUE,
    name VARCHAR(255) UNIQUE,
    location VARCHAR(255)
);

CREATE TABLE member (
    id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE,
    gender gender,
    address TEXT,
    profession VARCHAR(255),
    phone_number INTEGER,
    email VARCHAR(255) UNIQUE,
    occupation member_occupation,
    registration_fee_paid BOOLEAN DEFAULT FALSE,
    membership_dues_paid BOOLEAN DEFAULT FALSE,
    collectivity_id VARCHAR(50) REFERENCES collectivity(id)
);

CREATE TABLE sponsorship (
    id SERIAL PRIMARY KEY,
    candidate_id VARCHAR(50) REFERENCES member(id),
    sponsor_id VARCHAR(50) REFERENCES member(id),
    UNIQUE(candidate_id, sponsor_id)
);

CREATE TABLE collectivity_structure (
    collectivity_id VARCHAR(50) PRIMARY KEY REFERENCES collectivity(id),
    president_id VARCHAR(50) REFERENCES member(id),
    vice_president_id VARCHAR(50) REFERENCES member(id),
    treasurer_id VARCHAR(50) REFERENCES member(id),
    secretary_id VARCHAR(50) REFERENCES member(id)
);

CREATE TABLE membership_fee (
    id VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50) REFERENCES collectivity(id),
    eligible_from DATE,
    frequency frequency,
    amount DECIMAL(15, 2),
    label VARCHAR(255),
    status activity_status
);

CREATE TABLE financial_account (
    id VARCHAR(50) PRIMARY KEY,
    type VARCHAR(20), -- 'CASH', 'MOBILE_BANKING', 'BANK'
    amount DECIMAL(15, 2) DEFAULT 0.00,
    holder_name VARCHAR(255),
    mobile_service mobile_service_name,
    mobile_number INTEGER,
    bank_name bank_name,
    bank_code INTEGER,
    bank_branch_code INTEGER,
    bank_account_number INTEGER,
    bank_account_key INTEGER
);

CREATE TABLE member_payment (
    id VARCHAR(50) PRIMARY KEY,
    member_id VARCHAR(50) REFERENCES member(id),
    membership_fee_id VARCHAR(50) REFERENCES membership_fee(id),
    amount DECIMAL(15, 2),
    payment_mode payment_mode,
    account_credited_id VARCHAR(50) REFERENCES financial_account(id),
    creation_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE collectivity_transaction (
    id VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50) REFERENCES collectivity(id),
    creation_date DATE DEFAULT CURRENT_DATE,
    amount DECIMAL(15, 2),
    payment_mode payment_mode,
    account_credited_id VARCHAR(50) REFERENCES financial_account(id),
    member_debited_id VARCHAR(50) REFERENCES member(id)
);

