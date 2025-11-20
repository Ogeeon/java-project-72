package hexlet.code.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UrlCheck {
    @Setter
    private Long id;
    private final int statusCode;
    private final String title;
    private final String h1;
    private final String description;
    private final Long urlId;
    @Setter
    private LocalDateTime createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description, Long urlId) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.urlId = urlId;
    }
}
