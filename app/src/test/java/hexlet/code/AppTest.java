package hexlet.code;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void testReadResourceWithValidFile() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        var method = App.class.getDeclaredMethod("readResourceFile", String.class);
        method.setAccessible(true);
        var content = (String) method.invoke(null, "schema.sql");
        assertThat(content).isNotNull();
    }

    @Test
    void testReadResourceFileWithMissingFile() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        var method = App.class.getDeclaredMethod("readResourceFile", String.class);
        method.setAccessible(true);
        var content = (String) method.invoke(null, "nonexistent-file.txt");
        assertThat(content).isEmpty();
    }

    @Test
    void testGetApp() throws IOException, SQLException {
        var app = App.getApp();
        assertThat(app).isNotNull();
    }

}
