alter table task
    drop column publish_date,
    add column is_published boolean default false not null;