CREATE TABLE conversion_results (
    id UUID NOT NULL PRIMARY KEY,
    input_value NUMERIC(19, 6) NOT NULL,
    source_unit VARCHAR(255) NOT NULL,
    target_unit VARCHAR(255) NOT NULL,
    result NUMERIC(19, 6) NOT NULL
);
