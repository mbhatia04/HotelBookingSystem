CREATE SCHEMA IF NOT EXISTS hbs;

CREATE TABLE hbs.guest (
                           guest_id SERIAL PRIMARY KEY,
                           first_name VARCHAR(50) NOT NULL,
                           last_name VARCHAR(50) NOT NULL,
                           email VARCHAR(100) UNIQUE,
                           phone VARCHAR(20),
                           created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE hbs.stay (
                          stay_id SERIAL PRIMARY KEY,
                          guest_id INTEGER NOT NULL REFERENCES hbs.guest(guest_id) ON DELETE CASCADE,
                          check_in_date DATE NOT NULL,
                          check_out_date DATE NOT NULL,
                          room_number VARCHAR(10),
                          number_of_guests INTEGER,
                          special_requests TEXT,
                          created_at TIMESTAMP DEFAULT NOW(),
                          CHECK (check_in_date < check_out_date)
);

CREATE TABLE hbs.room_type (
                               room_id SERIAL PRIMARY KEY,
                               room_type VARCHAR(50) NOT NULL UNIQUE,
                               room_desc VARCHAR(256)
);

INSERT INTO hbs.room_type (room_type, room_desc) VALUES
                                                     ('King', 'Spacious king bed room'),
                                                     ('Queen', 'Cozy queen bed room'),
                                                     ('Double', 'Two double beds'),
                                                     ('King Suite', 'Luxury suite with king bed and sitting area'),
                                                     ('Queen Suite', 'Luxury suite with queen bed and sitting area');
commit;

CREATE TABLE hbs.charge (
                            charge_id SERIAL PRIMARY KEY,
                            stay_id INT REFERENCES hbs.stay(stay_id) ON DELETE CASCADE,
                            charge_date DATE NOT NULL,
                            charge_type VARCHAR(50) NOT NULL,
                            amount NUMERIC(10, 2) NOT NULL
);

ALTER TABLE hbs.room_type
    ADD COLUMN room_rate NUMERIC(10, 2);

UPDATE hbs.room_type SET room_rate = 120.00 WHERE room_type = 'King';
UPDATE hbs.room_type SET room_rate = 110.00 WHERE room_type = 'Queen';
UPDATE hbs.room_type SET room_rate = 100.00 WHERE room_type = 'Double';
UPDATE hbs.room_type SET room_rate = 180.00 WHERE room_type = 'King Suite';
UPDATE hbs.room_type SET room_rate = 170.00 WHERE room_type = 'Queen Suite';

ALTER TABLE hbs.stay ADD COLUMN checked_out_yn CHAR(1) DEFAULT 'N';
ALTER TABLE hbs.stay ADD COLUMN actual_check_out_date DATE;
