create table conversion_result (
    id uuid primary key,
    input_value numeric(38, 18) not null,
    source_unit varchar(32) not null,
    target_unit varchar(32) not null,
    result numeric(38, 18) not null
);
