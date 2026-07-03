create table unit (
    id uuid primary key,
    name varchar(255) not null unique,
    system varchar(32) not null
);
