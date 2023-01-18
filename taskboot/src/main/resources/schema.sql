CREATE TABLE IF NOT EXISTS consisttask
(
    task_id   int  PRIMARY KEY,
    name      TEXT not null,
    status    TEXT not null,
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