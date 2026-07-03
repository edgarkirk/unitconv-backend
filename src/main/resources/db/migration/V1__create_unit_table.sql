create table unit (
    id uuid primary key,
    name varchar(32) not null unique,
    system varchar(16) not null
);
