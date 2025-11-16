package hexlet.code.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Url {
    @Setter
    private Long id;
    private final String pageUrl;
    @Setter
    private LocalDateTime createdAt;

    public Url(String pageUrl) {
        this.pageUrl = pageUrl;
    }
    public Url(String pageUrl, LocalDateTime createdAt) {
        this.pageUrl = pageUrl;
        this.createdAt = createdAt;
    }
}