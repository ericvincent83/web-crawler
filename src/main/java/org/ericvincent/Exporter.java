package org.ericvincent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class Exporter {

    public static final Logger LOGGER = LogManager.getLogger(Exporter.class);

    public static boolean printToFile(Map<String, Set<String>> sitemap, Path outputFile) {
        try (FileWriter fileWriter = new FileWriter(outputFile.toString());
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (String key : sitemap.keySet()) {
                printWriter.println(key);
                sitemap.get(key).stream()
                        .map(resource -> "\t" + resource)
                        .forEach(printWriter::println);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Could not create %s, dumping to the console instead", outputFile.toString()));
            sitemap.forEach((key, value) -> System.out.println(key + " - " + value));
            return false;
        }
        return true;
    }
}
