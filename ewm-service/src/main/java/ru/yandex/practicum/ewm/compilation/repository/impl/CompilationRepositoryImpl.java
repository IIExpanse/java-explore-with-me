package ru.yandex.practicum.ewm.compilation.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.ewm.compilation.repository.CompilationRepositoryCustom;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class CompilationRepositoryImpl implements CompilationRepositoryCustom {

    private final JdbcTemplate template;

    @Override
    public void addEventsIntoCompilation(List<Long> eventIds, long compId) {
        String sql = "INSERT INTO events_compilations VALUES (?, ?)";

        template.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, eventIds.get(i));
                ps.setLong(2, compId);
            }

            @Override
            public int getBatchSize() {
                return eventIds.size();
            }
        });
    }
}
