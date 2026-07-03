CREATE TABLE conversion_result (
    id UUID PRIMARY KEY,
    input_value NUMERIC(19, 6) NOT NULL,
    source_unit VARCHAR(50) NOT NULL,
    target_unit VARCHAR(50) NOT NULL,
    result NUMERIC(19, 6) NOT NULL
);
