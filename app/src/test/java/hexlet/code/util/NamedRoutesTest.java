package hexlet.code.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class NamedRoutesTest {
    @Test
    void testRootPath() {
        assertEquals("/", NamedRoutes.rootPath());
    }

    @Test
    void testUrlsPath() {
        assertEquals("/urls", NamedRoutes.urlsPath());
    }

    @Test
    void testUrlPathWithLong() {
        assertEquals("/urls/123", NamedRoutes.urlPath(123L));
    }

    @Test
    void testUrlPathWithString() {
        assertEquals("/urls/{id}", NamedRoutes.urlPath("{id}"));
    }

    @Test
    void testPrivateConstructor() throws Exception {
        var constructor = NamedRoutes.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        var exception = assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("Utility class", exception.getCause().getMessage());
    }
}
