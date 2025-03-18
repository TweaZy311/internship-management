alter table application
    add column creation_date date not null default current_date;
alter table internship
    add column registration_start_date date not null default current_date;