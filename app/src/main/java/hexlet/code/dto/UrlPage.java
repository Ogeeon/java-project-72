package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;

import java.util.List;

@Getter
public class UrlPage extends BasePage {
    private final Url url;
    private final List<UrlCheck> checks;

    public UrlPage(Url url) {
        super();
        this.url = url;
        this.checks = null;
    }

    public UrlPage(Url url, List<UrlCheck> checks) {
        super();
        this.url = url;
        this.checks = checks;
    }
}
