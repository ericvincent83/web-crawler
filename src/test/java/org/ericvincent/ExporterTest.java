package org.ericvincent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExporterTest {

    private static final String OUTPUT = "output/testOutput.txt";
    private Path outputPath;
    private Map<String, Set<String>> sitemap;
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setup() throws IOException {
        outputPath = getResourcePath(OUTPUT);
        System.setOut(new PrintStream(outputStreamCaptor));
        if (Files.exists(outputPath)) {
            Files.delete(outputPath);
        }
        sitemap = Map.of(
                "http://www.example.org", Set.of("http://www.example.org/script.js", "http://www.example.org/img.png"),
                "http://www.facebook.com", Collections.emptySet());
    }

    @Test
    void printToFile_creates_and_populates_a_file() throws IOException {
        boolean fileWritten = Exporter.printToFile(sitemap, outputPath);
        assertTrue(fileWritten);
        assertTrue(Files.exists(outputPath));
        List<String> lines = Files.lines(outputPath)
                .collect(Collectors.toList());
        assertThat(lines, hasSize(4));
        assertThat(lines, containsInAnyOrder("http://www.example.org",
                "\thttp://www.example.org/script.js",
                "\thttp://www.example.org/img.png",
                "http://www.facebook.com"));
    }

    @Test
    void printToFile_dumps_to_stdout_when_file_cannot_be_written() {
        boolean fileWritten = Exporter.printToFile(sitemap, Path.of("qazwsxedcrf", "asxweffsdfs", "aseefdsf"));
        assertFalse(fileWritten);
        assertFalse(Files.exists(Path.of("qazwsxedcrf", "asxweffsdfs", "aseefdsf")));
        assertThat(outputStreamCaptor.toString(),
                anyOf(
                        containsString("http://www.example.org - [http://www.example.org/script.js, http://www.example.org/img.png]"),
                        containsString("http://www.example.org - [http://www.example.org/img.png, http://www.example.org/script.js]")));
        assertThat(outputStreamCaptor.toString(), containsString("http://www.facebook.com - []"));
    }

    private static Path getResourcePath(String filePath) {
        String rootpath = null;
        try {
            rootpath = Paths.get(ExporterTest.class.getResource("/").toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String substring = rootpath.substring(0, rootpath.indexOf("target"));

        return Paths.get(substring, "src/test/resources", filePath);
    }
}