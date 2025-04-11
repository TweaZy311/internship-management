create sequence audit_seq;

create table audit
(
    id          bigint       not null unique default nextval('audit_seq'),
    action_type varchar(255) not null,
    entity_type varchar(255) not null,
    entity_id   bigint       not null,
    entity_name varchar(255),
    error       varchar(255),
    user_id     bigint,
    primary key (id)
);

alter table if exists audit
    add constraint audit_user_fk foreign key (user_id) references app_user;