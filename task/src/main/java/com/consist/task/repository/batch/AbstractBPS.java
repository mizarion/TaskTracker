package com.consist.task.repository.batch;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

public abstract class AbstractBPS implements BatchPreparedStatementSetter {

    public abstract String getSQL();
}
