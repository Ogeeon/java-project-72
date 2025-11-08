package hexlet.code.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Url {
    @Setter
    private long id;
    private String name;
    private String url;
    private LocalDateTime createdAt;

    public Url(String name, String url) {
        this.name = name;
        this.url = url;
    }
}