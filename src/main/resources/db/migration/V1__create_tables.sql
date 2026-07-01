create table unit (
    id uuid primary key,
    name varchar(50) not null unique,
    system varchar(20) not null
);

create table conversion_result (
    id uuid primary key,
    input_value numeric(19, 6) not null,
    source_unit varchar(50) not null,
    target_unit varchar(50) not null,
    result numeric(19, 6) not null
);
