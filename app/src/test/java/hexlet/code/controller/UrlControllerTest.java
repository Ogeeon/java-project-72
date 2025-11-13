package hexlet.code.controller;

import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

class UrlControllerTest {

    private Javalin app;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        app = App.getApp();
        UrlRepository.removeAll();
    }

    @Test
    void testInstantiationException() {
        try {
            var constructor = UrlController.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            assertThat(e.getCause()).isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Utility class");
        }
    }
    
    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body();
            assertThat(body).isNotNull();
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
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/100");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testCreateAndDisplayUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            var body = response.body();
            assertThat(body).isNotNull();
            assertThat(body.string()).contains("https://example.com");

            response = client.get("/urls/1");
            assertThat(response.code()).isEqualTo(200);
            body = response.body();
            assertThat(body).isNotNull();
            assertThat(body.string()).contains("https://example.com");
        });
    }

    @Test
    void testCreateUrlFromMixedCase() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=HTTPS://EXAMple.COm";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            assertThat(urls.get(0).getPageUrl()).isEqualTo("https://example.com");
        });
    }

    @Test
    void testCreateUrlWithCustomPort() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=http://example.com:8080";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            assertThat(urls.get(0).getPageUrl()).isEqualTo("http://example.com:8080");
        });
    }

    @Test
    void testIgnorePathAndQuery() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com/path?query=value";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            assertThat(urls.get(0).getPageUrl()).isEqualTo("https://example.com");
        });
    }

    @Test
    void testCreateUrlWithWhitespace() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=  https://example.com  ";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            assertThat(urls.get(0).getPageUrl()).isEqualTo("https://example.com");
        });
    }

    @Test
    void testRejectInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=chepukha";
            var response = client.post("/urls", requestBody);
            var body = response.body();
            assertThat(body).isNotNull();
            assertThat(body.string()).contains("Некорректный URL");

            var urls = UrlRepository.getEntities();
            assertThat(urls).isEmpty();
        });
    }

    @Test
    void testRejectEmptyUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=";
            var response = client.post("/urls", requestBody);
            var body = response.body();
            assertThat(body).isNotNull();
            assertThat(body.string()).contains("Некорректный URL");

            var urls = UrlRepository.getEntities();
            assertThat(urls).isEmpty();
        });
    }

    @Test
    void testRejectDuplicateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response1 = client.post("/urls", requestBody);
            assertThat(response1.code()).isEqualTo(200);

            var response2 = client.post("/urls", requestBody);
            assertThat(response2.code()).isEqualTo(200);
            var body = response2.body();
            assertThat(body).isNotNull();
            assertThat(body.string()).contains("Страница уже существует");

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
        });
    }

    @Test
    void testIndexDisplaysAllUrls() {
        JavalinTest.test(app, (server, client) -> {
            var url1 = new Url("https://example.com");
            var url2 = new Url("https://google.com");
            UrlRepository.save(url1);
            UrlRepository.save(url2);

            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            var body = response.body();
            assertThat(body).isNotNull();
            var bodyString = body.string();
            assertThat(bodyString).contains("https://example.com");
            assertThat(bodyString).contains("https://google.com");
        });
    }

    @Test
    void testShowDisplaysUrl() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://hexlet.io");
            UrlRepository.save(url);

            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            var body = response.body();
            assertThat(body).isNotNull();
            var bodyString = body.string();
            assertThat(bodyString).contains("ID");
            assertThat(bodyString).contains("https://hexlet.io");
            assertThat(bodyString).contains("Дата создания");
        });
    }

    @Test
    void testShowNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999");
            assertThat(response.code()).isEqualTo(404);
            var body = response.body();
            assertThat(body).isNotNull();
            var bodyString = body.string();
            assertThat(bodyString).contains("999");
            assertThat(bodyString).contains("не найдена");
        });
    }

    @Test
    void testConsumesFlashMessages() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            var body = response.body();
            assertThat(body).isNotNull();
            assertThat(body.string()).contains("Страница успешно добавлена");
            response = client.get("/urls");
            body = response.body();
            assertThat(body).isNotNull();
            assertThat(body.string()).doesNotContain("Страница успешно добавлена");
        });
    }
}
