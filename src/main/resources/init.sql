-- Create Enums
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE member_status AS ENUM ('JUNIOR', 'CONFIRMED');
CREATE TYPE post_name AS ENUM ('PRESIDENT', 'VICE_PRESIDENT', 'TREASURER', 'SECRETARY');
CREATE TYPE entity_type AS ENUM ('FEDERATION', 'COLLECTIVITY');
CREATE TYPE account_type AS ENUM ('CASH', 'BANK', 'MOBILE_MONEY');
CREATE TYPE payment_type AS ENUM ('ADMISSION_FEE', 'ANNUAL_FEE', 'MONTHLY_FEE', 'PUNCTUAL');
CREATE TYPE payment_mode AS ENUM ('CASH', 'BANK_TRANSFER', 'MOBILE_MONEY');
CREATE TYPE activity_type AS ENUM ('GENERAL_ASSEMBLY', 'JUNIOR_TRAINING', 'EXCEPTIONAL');
CREATE TYPE bank_name AS ENUM ('BRED', 'MCB', 'BMOI', 'BOA', 'BGFI', 'AFG', 'ACCÈS BANQUE', 'BAOBAB', 'SIPEM');
CREATE TYPE mobile_service_name AS ENUM ('ORANGE_MONEY', 'MVOLA', 'AIRTEL_MONEY');

-- Tables
CREATE TABLE federation (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL DEFAULT 'Fédération Nationale des Collectivités Agricoles',
    creation_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE collectivity (
    id SERIAL PRIMARY KEY,
    number VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    specialty VARCHAR(255),
    city VARCHAR(100),
    creation_date DATE DEFAULT CURRENT_DATE,
    annual_fee DECIMAL(15, 2) DEFAULT 0.00
);

CREATE TABLE member (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE,
    gender gender,
    address TEXT,
    profession VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255) UNIQUE,
    admission_date DATE DEFAULT CURRENT_DATE
);

CREATE TABLE membership (
    id SERIAL PRIMARY KEY,
    member_id INTEGER REFERENCES member(id),
    collectivity_id INTEGER REFERENCES collectivity(id),
    status member_status DEFAULT 'JUNIOR',
    registration_date DATE DEFAULT CURRENT_DATE,
    UNIQUE(member_id, collectivity_id)
);

CREATE TABLE mandate (
    id SERIAL PRIMARY KEY,
    entity_id INTEGER NOT NULL, -- references federation or collectivity
    entity_type entity_type NOT NULL,
    year INTEGER NOT NULL,
    start_date DATE,
    end_date DATE
);

CREATE TABLE post_assignment (
    id SERIAL PRIMARY KEY,
    mandate_id INTEGER REFERENCES mandate(id),
    member_id INTEGER REFERENCES member(id),
    post_name post_name NOT NULL,
    UNIQUE(mandate_id, post_name) -- Only one person per post in a mandate
);

CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    owner_id INTEGER NOT NULL, -- references federation or collectivity
    owner_type entity_type NOT NULL,
    type account_type NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'MGA',
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bank_details (
    id SERIAL PRIMARY KEY,
    account_id INTEGER UNIQUE REFERENCES account(id),
    owner_name VARCHAR(255),
    bank_name bank_name,
    bank_code CHAR(5),
    branch_code CHAR(5),
    account_number CHAR(11),
    rib_key CHAR(2)
);

CREATE TABLE mobile_money_details (
    id SERIAL PRIMARY KEY,
    account_id INTEGER UNIQUE REFERENCES account(id),
    owner_name VARCHAR(255),
    service_name mobile_service_name,
    phone_number VARCHAR(20)
);

CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    member_id INTEGER REFERENCES member(id),
    collectivity_id INTEGER REFERENCES collectivity(id),
    amount DECIMAL(15, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type payment_type NOT NULL,
    mode payment_mode NOT NULL
);

CREATE TABLE activity (
    id SERIAL PRIMARY KEY,
    collectivity_id INTEGER REFERENCES collectivity(id), -- Null for Federation activities
    title VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    type activity_type NOT NULL,
    is_mandatory BOOLEAN DEFAULT FALSE
);

CREATE TABLE attendance (
    id SERIAL PRIMARY KEY,
    activity_id INTEGER REFERENCES activity(id),
    member_id INTEGER REFERENCES member(id),
    is_present BOOLEAN DEFAULT FALSE,
    UNIQUE(activity_id, member_id)
);

CREATE TABLE sponsorship (
    id SERIAL PRIMARY KEY,
    candidate_id INTEGER REFERENCES member(id),
    sponsor_id INTEGER REFERENCES member(id),
    collectivity_id INTEGER REFERENCES collectivity(id),
    relation_type VARCHAR(100), -- Family, Friend, Colleague, etc.
    CONSTRAINT different_member CHECK (candidate_id != sponsor_id)
);

-- Seed Initial Data
INSERT INTO federation (name) VALUES ('Fédération Nationale Agricole de Madagascar');

INSERT INTO collectivity (number, name, specialty, city, annual_fee) VALUES 
('COL-001', 'Collectivité de Riziculture', 'Riziculture', 'Antsirabe', 200000.00),
('COL-002', 'Collectivité de Vanille', 'Vanille', 'Sambava', 300000.00);

INSERT INTO member (first_name, last_name, gender, profession, email) VALUES 
('Jean', 'Dupont', 'MALE', 'Agriculteur', 'jean.dupont@email.com'),
('Marie', 'Razafy', 'FEMALE', 'Ingénieur Agro', 'marie.razafy@email.com');
