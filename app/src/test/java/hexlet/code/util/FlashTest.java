package hexlet.code.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class FlashTest {
    @Test
    void testBootstrapClassSuccess() {
        assertEquals("alert-success", Flash.bootstrapClass(FlashType.SUCCESS));
    }

    @Test
    void testBootstrapClassError() {
        assertEquals("alert-danger", Flash.bootstrapClass(FlashType.ERROR));
    }

    @Test
    void testBootstrapClassWarning() {
        assertEquals("alert-warning", Flash.bootstrapClass(FlashType.WARNING));
    }

    @Test
    void testBootstrapClassInfo() {
        assertEquals("alert-info", Flash.bootstrapClass(FlashType.INFO));
    }

    @Test
    void testBootstrapClassNull() {
        assertEquals("alert-info", Flash.bootstrapClass(null));
    }
}
