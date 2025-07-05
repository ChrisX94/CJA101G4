package com.shakemate.servicecase.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EmailColumnInitializer implements ApplicationRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public EmailColumnInitializer(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!columnExists("servicecase", "EMAIL")) {
            addEmailColumn();
        }
    }

    private boolean columnExists(String tableName, String columnName) throws SQLException {
        try (var conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, tableName, columnName)) {
                return rs.next();
            }
        }
    }

    private void addEmailColumn() {
        try {
            String ddl = "ALTER TABLE servicecase ADD COLUMN EMAIL VARCHAR(255) NOT NULL DEFAULT ''";
            jdbcTemplate.execute(ddl);
            System.out.println("[Init] ✅ 已新增 EMAIL 欄位到 servicecase 表");
        } catch (Exception e) {
            System.err.println("[Init] ⚠️ 新增 EMAIL 欄位失敗：" + e.getMessage());
        }
    }
}
