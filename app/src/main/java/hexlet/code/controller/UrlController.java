package hexlet.code.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.FlashType;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import static io.javalin.rendering.template.TemplateUtil.model;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlController {
    private static final String MAIN_PAGE_JTE = "index.jte";
    private static final String URLS_PAGE_JTE = "urls/index.jte";
    private static final String URL_PAGE_JTE = "urls/show.jte";
    private static final String ATTR_FLASH = "flash";
    private static final String ATTR_FLASH_TYPE = "flashType";

    private UrlController() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static String getUrlFromString(String input) {
        try {
            var urlObj = (new URI(input.trim())).toURL();
            return String.format("%s://%s%s", urlObj.getProtocol(),
                    urlObj.getHost().toLowerCase(),
                    (urlObj.getPort() == -1 ? "" : ":" + urlObj.getPort()));
        } catch (NullPointerException | MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            log.info("Incorrect url passed: {}", input);
            return null;
        }
    }

    public static void create(Context ctx) {
        var urlParam  = ctx.formParam("url");
        String pageUrl = getUrlFromString(urlParam);
        if (pageUrl == null) {
            var page = new MainPage();
            ctx.render(MAIN_PAGE_JTE, model(
                "page", page,
                "flash", "Некорректный URL",
                "flashType", FlashType.ERROR)
            );
            return;
        }

        try {
            if (UrlRepository.pageUrlExists(pageUrl)) {
                var page = new MainPage();
                ctx.render(MAIN_PAGE_JTE, model(
                    "page", page,
                    "flash", "Страница уже существует",
                    "flashType", FlashType.ERROR)
                );
            } else {
                var urlObj = new Url(pageUrl);
                UrlRepository.save(urlObj);
                var page = new MainPage();
                ctx.render(MAIN_PAGE_JTE, model(
                    "page", page,
                    "flash", "Страница успешно добавлена",
                    "flashType", FlashType.SUCCESS)
                );
            }
        } catch (SQLException e) {
            log.error("SQL exception on saving url: {}", e.getMessage());
            ctx.sessionAttribute(ATTR_FLASH, "Произошла ошибка при добавлении страницы");
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.ERROR);
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void index(Context ctx) {
        try {
            var urls = UrlRepository.getEntities();
            var page = new UrlsPage(urls);
            ctx.render(URLS_PAGE_JTE, model(
                "page", page,
                ATTR_FLASH, ctx.consumeSessionAttribute(ATTR_FLASH),
                ATTR_FLASH_TYPE, ctx.consumeSessionAttribute(ATTR_FLASH_TYPE)
            ));
        } catch (SQLException e) {
            log.error("SQL exception on getting urls list: {}", e.getMessage());
            ctx.sessionAttribute(ATTR_FLASH, "Произошла ошибка при получении списка страниц");
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.ERROR);
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void show(Context ctx) {
        try {
            var urlOpt = UrlRepository.find(Long.valueOf(ctx.pathParam("id")));
            if (urlOpt.isEmpty()) {
                var page = new BasePage();
                ctx.status(404);
                ctx.render("layout/page.jte", model(
                    "page", page,
                    ATTR_FLASH, "Страница с id = " + ctx.pathParam("id") + " не найдена",
                    ATTR_FLASH_TYPE, FlashType.ERROR
                ));
                return;
            }
            var page = new UrlPage(urlOpt.get());
            ctx.render(URL_PAGE_JTE, model(
                "page", page,
                ATTR_FLASH, ctx.consumeSessionAttribute(ATTR_FLASH),
                ATTR_FLASH_TYPE, ctx.consumeSessionAttribute(ATTR_FLASH_TYPE),
                "activeNav", NamedRoutes.urlsPath()
            ));
        } catch (SQLException e) {
            log.error("SQL exception on getting url by id [{}]: {}", ctx.pathParam("id"), e.getMessage());
            ctx.sessionAttribute(ATTR_FLASH, "Произошла ошибка при получении страницы");
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.ERROR);
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }
}
