package org.ericvincent;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class CrawlerTest {

    @Test
    void crawl_extracts_links_and_static_resources_from_website_with_default_depth() {
        Parser parserMock = Mockito.mock(Parser.class);
        Document documentMock = Mockito.mock(Document.class);
        when(parserMock.parse(anyString())).thenReturn(documentMock);
        when(parserMock.extractLinks(documentMock))
                .thenReturn(Set.of("https://www.example.org",
                        "https://www.example.org/internallink",
                        "https://www.example.org/internallink2",
                        "https://www.facebook.com"));
        when(parserMock.extractStaticResources(documentMock))
                .thenReturn(Set.of("https://www.example.org/script.js",
                        "https://www.example.org/img.png"));

        Map<String, Set<String>> map = new Crawler(parserMock).crawl("https://www.example.org");

        assertThat(map.keySet(), hasSize(4));
        assertThat(map.keySet(), containsInAnyOrder("https://www.example.org",
                "https://www.example.org/internallink",
                "https://www.example.org/internallink2",
                "https://www.facebook.com"));
        map.keySet().stream()
                .filter(k -> k.startsWith("https://www.example.org"))
                .map(map::get)
                .forEach(values -> {
                    assertThat(values, hasSize(2));
                    assertThat(values, containsInAnyOrder("https://www.example.org/script.js",
                            "https://www.example.org/img.png"));
                });
    }

    @Test
    void crawl_extracts_links_and_static_resources_from_website_with_maxDepth_1() {
        Parser parserMock = Mockito.mock(Parser.class);
        Document documentMock = Mockito.mock(Document.class);
        when(parserMock.parse(anyString())).thenReturn(documentMock);
        when(parserMock.extractLinks(documentMock))
                .thenReturn(Set.of("https://www.example.org",
                        "https://www.example.org/internallink",
                        "https://www.example.org/internallink2",
                        "https://www.facebook.com"));
        when(parserMock.extractStaticResources(documentMock))
                .thenReturn(Set.of("https://www.example.org/script.js",
                        "https://www.example.org/img.png"));

        Map<String, Set<String>> map = new Crawler(parserMock).crawl("https://www.example.org", 1);

        assertThat(map.keySet(), hasSize(2));
        assertThat(map.keySet(), containsInAnyOrder("https://www.example.org", "https://www.facebook.com"));
        map.keySet().stream()
                .filter(k -> k.startsWith("https://www.example.org"))
                .map(map::get)
                .forEach(values -> {
                    assertThat(values, hasSize(2));
                    assertThat(values, containsInAnyOrder("https://www.example.org/script.js",
                            "https://www.example.org/img.png"));
                });
    }

    @Test
    void crawl_stops_if_one_link_is_broken() {
        Parser parserMock = Mockito.mock(Parser.class);
        Document documentMock = Mockito.mock(Document.class);
        when(parserMock.parse(anyString()))
                .thenReturn(documentMock)
                .thenThrow(RuntimeException.class);
        when(parserMock.extractLinks(documentMock))
                .thenReturn(Set.of("https://www.example.org",
                        "https://www.example.org/internallink",
                        "https://www.example.org/internallink2",
                        "https://www.facebook.com"));
        when(parserMock.extractStaticResources(documentMock))
                .thenReturn(Set.of("https://www.example.org/script.js",
                        "https://www.example.org/img.png"));

        assertThrows(RuntimeException.class, () -> new Crawler(parserMock).crawl("https://www.example.org", 2));
    }
}