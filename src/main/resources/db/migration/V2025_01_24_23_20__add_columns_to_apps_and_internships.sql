ALTER TABLE application
    ADD COLUMN creation_date DATE NOT NULL DEFAULT current_date;
ALTER TABLE internship
    ADD COLUMN registration_start_date DATE NOT NULL DEFAULT current_date;