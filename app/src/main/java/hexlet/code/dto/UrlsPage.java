package hexlet.code.dto;

import hexlet.code.model.Url;
import java.util.List;

import lombok.Getter;

@Getter
public class UrlsPage extends BasePage {
    private final List<Url> urls;

    public UrlsPage(List<Url> urls) {
        super();
        this.urls = urls;
    }
}
