create table unit (
    id uuid primary key,
    name varchar(255) not null unique,
    system varchar(32) not null
);

create table conversion_result (
    id uuid primary key,
    input_value numeric(19, 6) not null,
    source_unit varchar(255) not null,
    target_unit varchar(255) not null,
    result numeric(19, 6) not null
);
