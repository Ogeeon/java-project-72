package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.Getter;

@Getter
public class UrlPage extends BasePage {
    private final Url url;
    public UrlPage(Url url) {
        super();
        this.url = url;
    }
}
