ALTER TABLE applications
    ADD COLUMN creation_date DATE NOT NULL DEFAULT current_date;
ALTER TABLE internships
    ADD COLUMN registration_start_date DATE NOT NULL DEFAULT current_date;