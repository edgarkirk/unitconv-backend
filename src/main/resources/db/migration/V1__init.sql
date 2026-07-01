create table unit (
    id uuid primary key,
    name varchar(50) not null,
    system varchar(20) not null
);

create table conversion_result (
    id uuid primary key,
    input_value numeric(19,12) not null,
    source_unit varchar(50) not null,
    target_unit varchar(50) not null,
    result numeric(19,12) not null
);

insert into unit (id, name, system) values
    ('00000000-0000-0000-0000-000000000001', 'feet', 'imperial'),
    ('00000000-0000-0000-0000-000000000002', 'gallons', 'imperial'),
    ('00000000-0000-0000-0000-000000000003', 'kilometres', 'metric'),
    ('00000000-0000-0000-0000-000000000004', 'litres', 'metric'),
    ('00000000-0000-0000-0000-000000000005', 'metres', 'metric'),
    ('00000000-0000-0000-0000-000000000006', 'miles', 'imperial');
