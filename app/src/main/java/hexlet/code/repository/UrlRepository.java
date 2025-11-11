package hexlet.code.repository;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hexlet.code.model.Url;

public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (page_url, created_at) VALUES (?, ?)";
        try (var conn = getDataSource().getConnection();
                var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getPageUrl());
            var createdAt = LocalDateTime.now();
            stmt.setTimestamp(2, Timestamp.valueOf(createdAt));

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

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT page_url, created_at FROM urls WHERE id = ?";
        try (var conn = getDataSource().getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var pageUrl = resultSet.getString("page_url");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

                var url = new Url(pageUrl, createdAt);
                url.setId(id);
                url.setCreatedAt(createdAt);
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static boolean pageUrlExists(String pageUrl) throws SQLException {
        var sql = "SELECT count(page_url) cnt FROM urls WHERE page_url = ?";
        try (var conn = getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pageUrl);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("cnt") > 0;
            }
            return false;
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT id, page_url, created_at FROM urls ORDER BY id";
        try (var conn = getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var urls = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var pageUrl = resultSet.getString("page_url");
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                var url = new Url(pageUrl, createdAt);
                url.setId(id);
                urls.add(url);
            }
            return urls;
        }
    }
}