package hexlet.code.dto;

import java.util.List;

import hexlet.code.model.Url;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UrlsPage extends BasePage {
    private final List<Url> urls;
}
