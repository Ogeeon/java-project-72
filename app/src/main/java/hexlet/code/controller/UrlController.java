package hexlet.code.controller;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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

import io.javalin.http.NotFoundResponse;
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
        URL urlObj;
        try {
            urlObj = (new URI(input.trim().toLowerCase())).toURL();
        } catch (NullPointerException | MalformedURLException | URISyntaxException | IllegalArgumentException e) {
            log.info("Incorrect url passed: {}", input);
            return "";
        }
        return String.format(
            "%s://%s%s",
            urlObj.getProtocol(),
            urlObj.getHost(),
            (urlObj.getPort() == -1 ? "" : ":" + urlObj.getPort()));
    }

    public static void create(Context ctx) {
        var urlParam  = ctx.formParam("url");
        String name = getUrlFromString(urlParam);
        if (name.isEmpty()) {
            var page = new MainPage();
            ctx.render(MAIN_PAGE_JTE, model(
                "page", page,
                ATTR_FLASH, "Некорректный URL",
                ATTR_FLASH_TYPE, FlashType.ERROR)
            );
            return;
        }

        try {
            var url = UrlRepository.findByName(name);
            if (url.isPresent()) {
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
        } catch (SQLException e) {
            ctx.sessionAttribute(ATTR_FLASH, e.getMessage());
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.ERROR);
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void index(Context ctx) {
        List<Url> urls;
        Map<Long, UrlCheck> checks;
        try {
            urls = UrlRepository.getEntities();
            checks = UrlCheckRepository.getLatestChecks();
        } catch (SQLException e) {
            ctx.sessionAttribute(ATTR_FLASH, e.getMessage());
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.ERROR);
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }
        var page = new UrlsPage(urls, checks);
        ctx.render(URLS_PAGE_JTE, model(
            "page", page,
            ATTR_FLASH, ctx.consumeSessionAttribute(ATTR_FLASH),
            ATTR_FLASH_TYPE, ctx.consumeSessionAttribute(ATTR_FLASH_TYPE)
        ));
    }

    public static void show(Context ctx) {
        Url url;
        List<UrlCheck> checks;
        try {
            url = UrlRepository.find(Long.valueOf(ctx.pathParam("id")))
                    .orElseThrow(() -> new NotFoundResponse("Страница с id = " + ctx.pathParam("id") + " не найдена"));
            checks = UrlCheckRepository.getEntitiesByUrlId(url.getId());
        } catch (SQLException e) {
            ctx.sessionAttribute(ATTR_FLASH, e.getMessage());
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.ERROR);
            ctx.redirect(NamedRoutes.urlPath(ctx.pathParam("id")));
            return;
        }
        var page = new UrlPage(url, checks);
        ctx.render(URL_PAGE_JTE, model(
                "page", page,
                ATTR_FLASH, ctx.consumeSessionAttribute(ATTR_FLASH),
                ATTR_FLASH_TYPE, ctx.consumeSessionAttribute(ATTR_FLASH_TYPE),
                "activeNav", NamedRoutes.urlsPath()
        ));
    }

    public static void check(Context ctx) {
        try {
            var url = UrlRepository.find(Long.valueOf(ctx.pathParam("id")))
                    .orElseThrow(() -> new NotFoundResponse("Страница с id = " + ctx.pathParam("id") + " не найдена"));
            var requestStr = Unirest.get(url.getName()).asString();
            var status = requestStr.getStatus();
            var body = requestStr.getBody();
            var document = Jsoup.parse(body);
            var title = document.title();
            var h1Element = document.selectFirst("h1");
            var h1 = h1Element == null ? "" : h1Element.text();
            var decsrElement = document.selectFirst("meta[name=description]");
            var description = decsrElement == null ? "" : decsrElement.attr("content");
            var check = new UrlCheck(status, title, h1, description, url.getId());
            UrlCheckRepository.save(check);
            ctx.redirect(NamedRoutes.urlPath(url.getId()));
            ctx.sessionAttribute(ATTR_FLASH, "Страница успешно проверена");
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.SUCCESS);
        } catch (Exception e) {
            ctx.sessionAttribute(ATTR_FLASH, e.getMessage());
            ctx.sessionAttribute(ATTR_FLASH_TYPE, FlashType.ERROR);
            ctx.redirect(NamedRoutes.urlPath(ctx.pathParam("id")));
        }
    }
}
