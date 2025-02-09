create sequence message_seq;

create table if not exists message
(
    id          bigint        not null unique default nextval('message_seq'),
    text        varchar(1024) not null,
    sent_at     timestamp     not null,
    receiver_id bigint,
    sender_id   bigint,
    primary key (id)
);

alter table if exists message
    add constraint message_receiver_fk foreign key (receiver_id) references app_user,
    add constraint message_sender_fk foreign key (sender_id) references app_user;