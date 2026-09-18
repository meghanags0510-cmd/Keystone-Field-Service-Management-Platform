CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    phone VARCHAR(40),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    customer_id BIGINT REFERENCES customers(id),
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE sites (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    name VARCHAR(160) NOT NULL,
    address VARCHAR(300) NOT NULL,
    city VARCHAR(100) NOT NULL
);

CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(180) NOT NULL,
    description VARCHAR(2000),
    priority VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    sla_due_at TIMESTAMP NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    site_id BIGINT NOT NULL REFERENCES sites(id),
    assignee_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE work_order_status_history (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL REFERENCES work_orders(id),
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    changed_by_id BIGINT NOT NULL REFERENCES users(id),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(500)
);

CREATE TABLE parts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    sku VARCHAR(80) NOT NULL UNIQUE,
    unit_cost NUMERIC(12,2) NOT NULL,
    stock_quantity INTEGER NOT NULL CHECK (stock_quantity >= 0)
);

CREATE TABLE part_usage (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL REFERENCES work_orders(id),
    part_id BIGINT NOT NULL REFERENCES parts(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE time_logs (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL REFERENCES work_orders(id),
    technician_id BIGINT NOT NULL REFERENCES users(id),
    minutes INTEGER NOT NULL CHECK (minutes > 0),
    note VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_work_orders_status ON work_orders(status);
CREATE INDEX idx_work_orders_customer ON work_orders(customer_id);
CREATE INDEX idx_work_orders_assignee ON work_orders(assignee_id);
CREATE INDEX idx_work_orders_sla ON work_orders(sla_due_at);
