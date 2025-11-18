package hexlet.code.repository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hexlet.code.App;
import hexlet.code.model.Url;

class UrlRepositoryTest {

    @BeforeEach
    void setUp() throws IOException, SQLException {
        App.getApp();
        UrlRepository.removeAll();
    }

    @Test
    void testInstantiation() throws NoSuchMethodException {
        var constructor = UrlRepository.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance)
            .isInstanceOf(InvocationTargetException.class)
            .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testSaveValidUrl() throws SQLException {
        var url = new Url("https://example.com");
        UrlRepository.save(url);

        assertThat(url.getId()).isNotNull().isGreaterThan(0);
        assertThat(url.getCreatedAt()).isNotNull();
    }

    @Test
    void testSaveUrlWithMultipleUrls() throws SQLException {
        var url1 = new Url("https://example.com");
        var url2 = new Url("https://google.com");

        UrlRepository.save(url1);
        UrlRepository.save(url2);

        assertThat(url1.getId()).isNotNull().isEqualTo(1);
        assertThat(url2.getId()).isNotNull().isEqualTo(2);
    }

    @Test
    void testSaveNullUrl() {
        assertThatThrownBy(() -> UrlRepository.save(new Url(null)))
            .isInstanceOf(SQLException.class);
    }

    @Test
    void testFindExistingUrl() throws SQLException {
        var originalUrl = new Url("https://example.com");
        UrlRepository.save(originalUrl);

        var found = UrlRepository.find(originalUrl.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("https://example.com");
        assertThat(found.get().getId()).isEqualTo(originalUrl.getId());
    }

    @Test
    void testFindNonExistentUrl() throws SQLException {
        var found = UrlRepository.find(999L);
        assertThat(found).isEmpty();
    }

    @Test
    void testNameExistsForSavedUrl() throws SQLException {
        var url = new Url("https://example.com");
        UrlRepository.save(url);

        var exists = UrlRepository.nameExists("https://example.com");
        assertThat(exists).isTrue();
    }

    @Test
    void testNameExistsForNonExistentUrl() throws SQLException {
        var exists = UrlRepository.nameExists("https://nonexistent.com");
        assertThat(exists).isFalse();
    }

    @Test
    void testGetEntitiesWhenEmpty() throws SQLException {
        var urls = UrlRepository.getEntities();
        assertThat(urls).isEmpty();
    }

    @Test
    void testGetEntitiesMultipleUrls() throws SQLException {
        var url1 = new Url("https://example.com");
        var url2 = new Url("https://google.com");
        var url3 = new Url("https://hexlet.io");

        UrlRepository.save(url1);
        UrlRepository.save(url2);
        UrlRepository.save(url3);

        var urls = UrlRepository.getEntities();

        assertThat(urls).hasSize(3);
        assertThat(urls).extracting(Url::getName)
            .containsExactly("https://example.com", "https://google.com", "https://hexlet.io");
    }

    @Test
    void testRemoveAll() throws SQLException {
        var url1 = new Url("https://example.com");
        var url2 = new Url("https://google.com");
        var url3 = new Url("https://hexlet.io");

        UrlRepository.save(url1);
        UrlRepository.save(url2);
        UrlRepository.save(url3);

        UrlRepository.removeAll();

        var urls = UrlRepository.getEntities();
        assertThat(urls).isEmpty();
    }
}
