package org.ericvincent;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ParserTest {

    @Test
    @Disabled
    void parse_returns_a_doc() {
        Document doc = new Parser().parse("https://www.google.co.uk");
        assertEquals(doc.title(), "Google");
    }

    @Test
    void parse_throw_if_url_incorrect() {
        assertThrows(RuntimeException.class, () -> new Parser().parse("foo"));
    }

    @Test
    void extractLinks_extract_all_links_from_the_document() {
        Document documentMock = Mockito.mock(Document.class);
        Element elMock1 = Mockito.mock(Element.class);
        Element elMock2 = Mockito.mock(Element.class);
        when(documentMock.select("a")).thenReturn(new Elements(List.of(elMock1, elMock2)));
        when(elMock1.attr("href")).thenReturn("https://www.google.co.uk/link");
        when(elMock2.attr("href")).thenReturn("https://www.facebook.com");

        Set<String> links = new Parser().extractLinks(documentMock);
        assertThat(links, hasSize(2));
        assertThat(links, containsInAnyOrder("https://www.google.co.uk/link", "https://www.facebook.com"));
    }

    @Test
    void extractStaticResources_extract_images_and_scripts_from_the_given_document() {
        Document documentMock = Mockito.mock(Document.class);
        Element scriptMock = Mockito.mock(Element.class);
        Element imgMock = Mockito.mock(Element.class);
        when(documentMock.select("script")).thenReturn(new Elements(List.of(scriptMock)));
        when(documentMock.select("img")).thenReturn(new Elements(List.of(imgMock)));
        when(scriptMock.attr("src")).thenReturn("https://www.google.co.uk/link.js");
        when(imgMock.attr("src")).thenReturn("https://www.facebook.com/profile.png");

        Set<String> links = new Parser().extractStaticResources(documentMock);
        assertThat(links, hasSize(2));
        assertThat(links, containsInAnyOrder("https://www.google.co.uk/link.js", "https://www.facebook.com/profile.png"));
    }
}