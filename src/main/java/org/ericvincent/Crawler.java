package org.ericvincent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Crawler {

    private static final Logger LOGGER = LogManager.getLogger(Parser.class);

    private final Parser parser;

    public Crawler(Parser parser) {
        this.parser = parser;
    }

    public Map<String, Set<String>> crawl(String website) {
        return crawl(website, 0);
    }

    public Map<String, Set<String>> crawl(String website, int maxDepth) {
        LOGGER.info("Start crawling " + website);
        LOGGER.info("Max depth: " + (maxDepth == 0 ? "Infinite" : maxDepth));
        return crawl(website, website, maxDepth, 0, new HashMap<>());
    }

    private Map<String, Set<String>> crawl(String website, String currentLink, int maxDepth, int depth, Map<String, Set<String>> sitemap) {
        LOGGER.debug("Inspecting " + currentLink);
        if (!sitemap.containsKey(currentLink) && (maxDepth == 0 || depth < maxDepth)) {
            LOGGER.debug("Visiting :" + currentLink);
            depth++;
            Document doc = parser.parse(currentLink);

            LOGGER.debug("Storing visited link: " + currentLink);
            sitemap.put(currentLink, parser.extractStaticResources(doc));

            for (String lnk : parser.extractLinks(doc)) {
                if (isExternal(website, lnk)) {
                    LOGGER.debug("Storing external link: " + lnk);
                    sitemap.put(lnk, Collections.emptySet());
                } else {
                    crawl(website, lnk, maxDepth, depth, sitemap);
                }
            }
        }
        return sitemap;
    }

    private boolean isExternal(String website, String link) {
        return !link.startsWith(website);
    }
}
