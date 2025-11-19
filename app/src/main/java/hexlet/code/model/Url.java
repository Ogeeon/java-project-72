package hexlet.code.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Url {
    @Setter
    private Long id;
    private final String name;
    @Setter
    private LocalDateTime createdAt;

    public Url(String name) {
        this.name = name;
    }
    public Url(String name, LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }
}
