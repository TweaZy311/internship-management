create sequence application_seq;
create sequence internship_seq;
create sequence lesson_seq;
create sequence solution_seq;
create sequence task_seq;
create sequence app_user_seq;

create table application
(
    id               bigint       not null unique default nextval('application_seq'),
    about            varchar(1024),
    birth_date       date         not null,
    city             varchar(255) not null,
    course           int4,
    education_status varchar(255) not null,
    email            varchar(255) not null,
    faculty          varchar(255),
    full_name        varchar(255) not null,
    phone_number     varchar(255) not null,
    specialty        varchar(255),
    status           varchar(255) not null,
    telegram_id      varchar(255) not null,
    university       varchar(255),
    internship_id    bigint,
    primary key (id)
);
create table internship
(
    id                    bigint       not null unique default nextval('internship_seq'),
    description           varchar(2048),
    end_date              date         not null,
    name                  varchar(255) not null,
    registration_end_date date         not null,
    start_date            date         not null,
    status                varchar(255) not null,
    primary key (id)
);
create table lesson
(
    id            bigint        not null unique default nextval('lesson_seq'),
    description   varchar(2048) not null,
    is_published  boolean       not null,
    name          varchar(255)  not null,
    internship_id bigint,
    primary key (id)
);
create table solution
(
    id               bigint       not null unique default nextval('solution_seq'),
    checked_time     timestamp,
    comment          varchar(1024),
    commits          jsonb        not null,
    repository_url   varchar(255) not null,
    status           varchar(255) not null,
    task_id          bigint,
    user_id          bigint,
    primary key (id)
);
create table task
(
    id            bigint        not null unique default nextval('task_seq'),
    description   varchar(2048) not null,
    name          varchar(255)  not null,
    publish_date  date,
    repository    varchar(255)  not null,
    repository_id bigint        not null,
    lesson_id     bigint,
    primary key (id)
);
create table app_user
(
    id            bigint       not null unique default nextval('app_user_seq'),
    email         varchar(255) not null,
    name          varchar(255) not null,
    password      varchar(255) not null,
    role          varchar(255),
    username      varchar(255) not null,
    internship_id bigint,
    primary key (id)
);
alter table if exists solution
    add constraint repository_url_uk unique (repository_url);
alter table if exists app_user
    add constraint user_email_uk unique (email);
alter table if exists app_user
    add constraint user_username_uk unique (username);
alter table if exists application
    add constraint application_internship_fk foreign key (internship_id) references internship;
alter table if exists lesson
    add constraint lesson_internship_fk foreign key (internship_id) references internship;
alter table if exists solution
    add constraint solution_task_fk foreign key (task_id) references task;
alter table if exists solution
    add constraint solution_user_fk foreign key (user_id) references app_user;
alter table if exists task
    add constraint task_lesson_fk foreign key (lesson_id) references lesson;
alter table if exists app_user
    add constraint user_internship_fk foreign key (internship_id) references internship;
