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
