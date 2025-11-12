package hexlet.code;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

class AppTest {

    private Javalin app;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() throws IOException, SQLException {
        app = App.getApp();
        UrlRepository.removeAll();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body();
            if (body == null) {
                throw new AssertionError("Response body is null");
            }
            assertThat(body.string()).contains("Анализатор страниц");
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://ru.hexlet.io/");
            UrlRepository.save(url);
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testUrlNotFound() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/100");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            var body = response.body();
            if (body == null) {
                throw new AssertionError("Response body is null");
            }
            assertThat(body.string()).contains("https://example.com");

            response = client.get("/urls/1");
            assertThat(response.code()).isEqualTo(200);
            body = response.body();
            if (body == null) {
                throw new AssertionError("Response body is null");
            }
            assertThat(body.string()).contains("https://example.com");
        });
    }
}