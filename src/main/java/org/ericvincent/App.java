package org.ericvincent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class App {

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private static final int MAX_DEPTH = 2;
    private static final Path OUTPUT = Path.of("output","sitemap.txt");

    public static void main(String[] args) {
        String base = "https://wiprodigital.com";
        if (args.length > 0) {
            base = args[0];
        }
        int maxDepth = MAX_DEPTH;
        if (args.length > 1) {
            maxDepth = Integer.parseInt(args[2]);
        }

        LOGGER.info(String.format("Crawling %s with a max depth of %s", base, maxDepth));

        Map<String, Set<String>> sitemap = new Crawler(new Parser()).crawl(base, maxDepth);
        LOGGER.info(String.format("Found %s links and %s static resources on %s",
                sitemap.keySet().size(),
                sitemap.values().stream()
                        .mapToLong(Collection::size)
                        .sum(),
                base));

        if (Exporter.printToFile(sitemap, OUTPUT)) {
            LOGGER.info(String.format("%s has been crawled and you can find the result in %s", base, OUTPUT.toString()));
        }
    }


}
