CREATE TABLE unit (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    system VARCHAR(20) NOT NULL,
    CONSTRAINT uk_unit_name UNIQUE (name)
);
