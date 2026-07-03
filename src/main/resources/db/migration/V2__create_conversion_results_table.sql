create table conversion_results (
    id uuid primary key,
    input_value numeric(19,6) not null,
    source_unit varchar(50) not null,
    target_unit varchar(50) not null,
    result numeric(19,6) not null
);
