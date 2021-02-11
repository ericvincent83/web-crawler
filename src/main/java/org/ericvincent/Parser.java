package org.ericvincent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    private static final Logger LOGGER = LogManager.getLogger(Parser.class);

    private static final String LINK_TAG = "a";
    private static final String LINK_ATTRIBUTE = "href";
    private static final String[] STATIC_RESOURCE_TAGS = { "img", "script"};
    private static final String STATIC_RESOURCE_ATTRIBUTE = "src";

    public Document parse(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            LOGGER.fatal("Connection issue while connecting to " + url);
            throw new RuntimeException(e);
        }
    }

    public Set<String> extractLinks(Document doc) {
        return doc.select(LINK_TAG).stream()
                .map(l -> l.attr(LINK_ATTRIBUTE))
                .collect(Collectors.toSet());
    }

    public Set<String> extractStaticResources(Document doc) {
        return Stream.of(STATIC_RESOURCE_TAGS)
                .map(doc::select)
                .flatMap(Collection::stream)
                .map(staticRes -> staticRes.attr(STATIC_RESOURCE_ATTRIBUTE))
                .collect(Collectors.toSet());
    }
}
