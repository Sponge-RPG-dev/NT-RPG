CREATE TABLE IF NOT EXISTS nt_core_migrations (
    author VARCHAR(255),
    id VARCHAR(255),
    executed DATE,
    note TEXT
);