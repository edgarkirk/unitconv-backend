CREATE TABLE units (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    system VARCHAR(20) NOT NULL
);

CREATE TABLE conversion_results (
    id UUID PRIMARY KEY,
    input_value DECIMAL(19, 6) NOT NULL,
    source_unit VARCHAR(50) NOT NULL,
    target_unit VARCHAR(50) NOT NULL,
    result DECIMAL(19, 6) NOT NULL
);
