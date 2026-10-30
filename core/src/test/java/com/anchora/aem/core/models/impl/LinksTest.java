package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class LinksTest {

    private final AemContext context = AppAemContext.newAemContext();

    private String resolve(String link) {
        return Links.resolve(context.request(), link);
    }

    @Test
    void internalPagePathGetsHtmlExtension() {
        assertEquals("/content/anchora/us/en/joey-club.html", resolve("/content/anchora/us/en/joey-club"));
    }

    @Test
    void internalPagePathKeepsFragmentAndQuery() {
        assertEquals("/content/anchora/us/en.html#top", resolve("/content/anchora/us/en#top"));
        assertEquals("/content/anchora/us/en.html?a=b", resolve(" /content/anchora/us/en?a=b "));
    }

    @Test
    void otherLinksAreUnchanged() {
        assertEquals("https://example.com/page", resolve("https://example.com/page"));
        assertEquals("#joey-club", resolve("#joey-club"));
        assertEquals("mailto:help@example.com", resolve("mailto:help@example.com"));
        assertEquals("/content/dam/anchora/file.pdf", resolve("/content/dam/anchora/file.pdf"));
        assertEquals("/content/anchora/us/en.html", resolve("/content/anchora/us/en.html"));
    }

    @Test
    void blankLinkResolvesToNull() {
        assertNull(resolve(null));
        assertNull(resolve("  "));
    }

    @Test
    void newTabLinkHasTargetAndRel() {
        LinkItem link = Links.create(context.request(), "Example", "https://example.com", true);
        assertEquals("_blank", link.getTarget());
        assertEquals("noopener noreferrer", link.getRel());
    }

    @Test
    void sameTabLinkHasNoTargetOrRel() {
        LinkItem link = Links.create(context.request(), "Home", "/content/anchora/us/en", false);
        assertEquals("/content/anchora/us/en.html", link.getHref());
        assertNull(link.getTarget());
        assertNull(link.getRel());
    }

    @Test
    void linkWithoutLabelOrTargetIsSkipped() {
        assertNull(Links.create(context.request(), " ", "/content/anchora/us/en", false));
        assertNull(Links.create(context.request(), "Home", null, false));
    }

    @Test
    void readsMultifieldInOrderAndSkipsIncompleteItems() {
        Resource multifield = context.create().resource("/content/test/links");
        context.create().resource(multifield, "item0", "label", "Home", "link", "/content/anchora/us/en");
        context.create().resource(multifield, "item1", "label", "No target");
        context.create().resource(multifield, "item2", "label", "External", "link", "https://example.com",
                "newTab", true);

        List<LinkItem> links = Links.fromMultifield(context.request(), multifield);

        assertEquals(2, links.size());
        assertEquals("Home", links.get(0).getLabel());
        assertEquals("External", links.get(1).getLabel());
        assertTrue(links.get(1).isNewTab());
    }

    @Test
    void missingMultifieldGivesEmptyList() {
        assertTrue(Links.fromMultifield(context.request(), null).isEmpty());
    }
}
