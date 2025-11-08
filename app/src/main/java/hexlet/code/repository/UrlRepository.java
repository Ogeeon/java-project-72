package hexlet.code.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import hexlet.code.model.Url;

public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, url, created_at) VALUES (?, ?, ?)";
        try (var conn = getDataSource().getConnection();
            var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, url.getName());
                stmt.setString(2, url.getUrl());
                var createdAt = LocalDateTime.now();
                stmt.setTimestamp(3, Timestamp.valueOf(createdAt));
                
                stmt.executeUpdate();
                var generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                    url.setCreatedAt(createdAt);
                } else {
                    throw new SQLException("DB did not return generated key");
                }
            }
    }
}