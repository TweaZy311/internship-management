create sequence status_seq;

create table status
(
    id   bigint       not null unique default nextval('status_seq'),
    name varchar(255) not null,
    type varchar(255) not null,
    primary key (id)
);

alter table if exists application
    drop column status,
    add column status_id bigint,
    add constraint application_status_fk foreign key (status_id) references status;
alter table if exists application
    drop column education_status,
    add column education_status_id bigint,
    add constraint application_education_status_fk foreign key (education_status_id) references status;
alter table if exists internship
    drop column status,
    add column status_id bigint,
    add constraint internship_status_fk foreign key (status_id) references status;
alter table if exists task_solution
    drop column status,
    add column status_id bigint,
    add constraint task_solution_status_fk foreign key (status_id) references status;