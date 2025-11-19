package hexlet.code.dto;

import java.util.List;
import java.util.Map;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UrlsPage extends BasePage {
    private final List<Url> urls;
    private final Map<Long, UrlCheck> checks;
}
