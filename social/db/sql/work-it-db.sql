CREATE TABLE trainers (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE participants (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE to_trainer_requests (
    id UUID PRIMARY KEY,
    participant_id UUID NOT NULL UNIQUE REFERENCES participants ON DELETE CASCADE
);

CREATE TABLE preferences (
    id UUID PRIMARY KEY,
    participant_id UUID REFERENCES participants ON DELETE CASCADE,
    trainer_id UUID REFERENCES participants ON DELETE CASCADE,
    value VARCHAR(50) NOT NULL UNIQUE
);