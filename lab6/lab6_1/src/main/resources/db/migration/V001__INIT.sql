-- Create the "customers" table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

-- Insert sample data
INSERT INTO customers (name, email) VALUES ('John Doe', 'john.doe@example.com');
INSERT INTO customers (name, email) VALUES ('Jane Smith', 'jane.smith@example.com');