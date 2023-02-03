CREATE TYPE status_type AS ENUM ('READY', 'RUNNING', 'FINISHED','CANCELED');

CREATE TABLE IF NOT EXISTS consisttask
(
    task_id   int PRIMARY KEY,
    name      TEXT        not null,
    status    status_type not null,
    parent_id int
);

CREATE TABLE IF NOT EXISTS taskparameters
(
    param_id   SERIAL PRIMARY KEY,
    type       TEXT not null,
    param_name TEXT not null,
    value      TEXT not null,
    task_id    int references consisttask (task_id)
        ON DELETE CASCADE
);