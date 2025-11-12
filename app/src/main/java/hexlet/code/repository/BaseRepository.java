package hexlet.code.repository;

import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;

public abstract class BaseRepository {
    @Getter
    @Setter
    private static HikariDataSource dataSource;

    protected BaseRepository() {
        throw new UnsupportedOperationException("Utility class, should not be instantiated");
    }
}
