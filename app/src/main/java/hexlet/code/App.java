package hexlet.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.UrlController;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.MainPage;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.FlashType;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class App {
    private static final String ATTR_FLASH = "flash";
    private static final String ATTR_FLASH_TYPE = "flashType";

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    private static String getJdbcUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } else {
            log.error("Failed to access {}", fileName);
            return "";
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static void main(String[] args) throws IOException, SQLException {
        var app = getApp();
        var port = getPort();
        log.info("Starting application with listening on port {}", port);
        app.start(port);
    }

    public static Javalin getApp() throws IOException, SQLException {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getJdbcUrl());
        hikariConfig.setMaximumPoolSize(10);

        var dataSource = new HikariDataSource(hikariConfig);
        // There won't be this env variable locally. It can be set to "true" on Render if needed.
        if (System.getenv().getOrDefault("RECREATE_SCHEMA", "true").equalsIgnoreCase("true")) {
            var sql = readResourceFile("schema.sql");
            if (!sql.isEmpty()) {
                log.info("Running recreate schema script");
                try (var connection = dataSource.getConnection();
                     var statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            } else {
                log.error("Failed to read schema.sql");
            }
        }

        BaseRepository.setDataSource(dataSource);
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get(NamedRoutes.rootPath(), ctx -> {
            var page = new MainPage();
            ctx.render("index.jte", model(
                "page", page,
                ATTR_FLASH, ctx.consumeSessionAttribute(ATTR_FLASH),
                ATTR_FLASH_TYPE, ctx.consumeSessionAttribute(ATTR_FLASH_TYPE)
            ));
        });
        app.post(NamedRoutes.urlsPath(), UrlController::create);
        app.get(NamedRoutes.urlsPath(), UrlController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlController::show);
        app.post(NamedRoutes.checkPath("{id}"), UrlController::check);
        app.exception(NotFoundResponse.class, (e, ctx) -> {
            var page = new BasePage();
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.render("layout/page.jte", model(
                    "page", page,
                    ATTR_FLASH, e.getMessage(),
                    ATTR_FLASH_TYPE, FlashType.ERROR
            ));
        });
        app.exception(SQLException.class, (e, ctx) -> {
            var page = new BasePage();
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.render("layout/page.jte", model(
                    "page", page,
                    ATTR_FLASH, e.getMessage(),
                    ATTR_FLASH_TYPE, FlashType.ERROR
            ));
        });
        return app;
    }
}
