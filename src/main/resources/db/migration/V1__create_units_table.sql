create table units (
    id uuid primary key,
    name varchar(50) not null unique,
    system varchar(20) not null,
    constraint chk_units_system check (system in ('metric', 'imperial'))
);
