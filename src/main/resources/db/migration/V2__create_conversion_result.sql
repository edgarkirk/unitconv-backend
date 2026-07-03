CREATE TABLE conversion_result (
    id UUID PRIMARY KEY,
    input_value NUMERIC(38, 16) NOT NULL,
    source_unit VARCHAR(100) NOT NULL,
    target_unit VARCHAR(100) NOT NULL,
    result NUMERIC(38, 16) NOT NULL
);
