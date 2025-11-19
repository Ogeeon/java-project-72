package hexlet.code.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.FlashType;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import static io.javalin.rendering.template.TemplateUtil.model;

import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

@Slf4j
public final class UrlController {
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

    public static void create(Context ctx) throws SQLException {
        var urlParam  = ctx.formParam("url");
        String name = getUrlFromString(urlParam);
        if (name == null) {
            var page = new MainPage();
            ctx.render(MAIN_PAGE_JTE, model(
                "page", page,
                ATTR_FLASH, "Некорректный URL",
                ATTR_FLASH_TYPE, FlashType.ERROR)
            );
            return;
        }

        if (UrlRepository.nameExists(name)) {
            var page = new MainPage();
            ctx.render(MAIN_PAGE_JTE, model(
                "page", page,
                ATTR_FLASH, "Страница уже существует",
                ATTR_FLASH_TYPE, FlashType.ERROR)
            );
        } else {
            var urlObj = new Url(name);
            UrlRepository.save(urlObj);
            ctx.sessionAttribute(ATTR_FLASH, "Страница успешно добавлена");
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.SUCCESS);
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var checks = UrlCheckRepository.getLatestChecks();
        var page = new UrlsPage(urls, checks);
        ctx.render(URLS_PAGE_JTE, model(
            "page", page,
            ATTR_FLASH, ctx.consumeSessionAttribute(ATTR_FLASH),
            ATTR_FLASH_TYPE, ctx.consumeSessionAttribute(ATTR_FLASH_TYPE)
        ));
    }

    public static void show(Context ctx) throws SQLException {
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
        var checks = UrlCheckRepository.getEntitiesByUrlId(urlOpt.get().getId());
        var page = new UrlPage(urlOpt.get(), checks);
        ctx.render(URL_PAGE_JTE, model(
            "page", page,
            ATTR_FLASH, ctx.consumeSessionAttribute(ATTR_FLASH),
            ATTR_FLASH_TYPE, ctx.consumeSessionAttribute(ATTR_FLASH_TYPE),
            "activeNav", NamedRoutes.urlsPath()
        ));
    }

    public static void check(Context ctx) throws SQLException {
        var urlId = Long.valueOf(ctx.pathParam("id"));
        var urlOpt = UrlRepository.find(urlId);
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
        var url = urlOpt.get();
        var requestStr = Unirest.get(url.getName()).asString();
        var status = requestStr.getStatus();
        var body = requestStr.getBody();
        var document = Jsoup.parse(body);
        var title = document.title();
        var h1Element = document.selectFirst("h1");
        var h1 = h1Element == null ? "" : h1Element.text();
        var decsrElement = document.selectFirst("meta[name=description]");
        var description = decsrElement == null ? "" : decsrElement.attr("content");
        var check = new UrlCheck(status, title, h1, description, url.getId(), LocalDateTime.now());
        UrlCheckRepository.save(check);
        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
