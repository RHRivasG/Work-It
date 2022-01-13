-- SCHEMA CREATION

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
    id SERIAL PRIMARY KEY,
    participant_id UUID REFERENCES participants ON DELETE CASCADE,
    trainer_id UUID REFERENCES trainers ON DELETE CASCADE,
    value VARCHAR(50) NOT NULL
);

-- INSERTING DEFAULT PREFERENCES

INSERT INTO preferences(value) VALUES ('Legs');
INSERT INTO preferences(value) VALUES ('Arms');
INSERT INTO preferences(value) VALUES ('Body');
INSERT INTO preferences(value) VALUES ('Quads');
INSERT INTO preferences(value) VALUES ('Man');
INSERT INTO preferences(value) VALUES ('Woman');

-- INSERTING TEST PARTICIPANT

INSERT INTO participants VALUES ('8f61fc06-1fd6-4421-9091-e29f1191648e', 'PruebaEncriptacion', '123456aA$');

INSERT INTO participants VALUES ('b8dc009f-e7b6-48b0-8685-63bbcf8153a9', 'EntrenadorPrueba', '123456aA$');