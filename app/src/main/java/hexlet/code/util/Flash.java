package hexlet.code.util;

public final class Flash {
    private Flash() { }

    public static String bootstrapClass(FlashType type) {
        if (type == null) {
            return "alert-info";
        }
        return switch (type) {
            case SUCCESS -> "alert-success";
            case ERROR -> "alert-danger";
            case WARNING -> "alert-warning";
            case INFO -> "alert-info";
        };
    }
}
