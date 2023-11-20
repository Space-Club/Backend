package com.spaceclub.global.log;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

@Slf4j
public class PrepareStateInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        log.info("sql={}", sql);
        return sql;
    }

}
