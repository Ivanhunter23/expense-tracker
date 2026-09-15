CREATE TABLE expenses(
        id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        description VARCHAR(255) NOT NULL,
        amount NUMERIC(10, 2) NOT NULL,
        category VARCHAR(50) NOT NULL,
        date DATE NOT NULL,

        CONSTRAINT amount_positive CHECK (amount > 0),
        CONSTRAINT valid_category CHECK(
        category IN('FOOD',
        'TRANSPORT',
        'ENTERTAINMENT',
        'HEALTH',
        'UTILITIES',
        'OTHER'
        )
    )
);
