package com.anchora.aem.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.anchora.aem.core.models.LinkItem;
import com.anchora.aem.core.models.Teaser;
import com.anchora.aem.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class TeaserImplTest {

    private static final String TEASER = "/content/anchora/us/en/test/jcr:content/teaser";
    private static final String ASSET = "/content/dam/anchora/joey.jpg";

    private final AemContext context = AppAemContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.addModelsForClasses(TeaserImpl.class);
        context.create().page("/content/anchora/us/en/test");
        context.create().asset(ASSET, 1200, 800, "image/jpeg",
                Map.of("tiff:ImageWidth", 1200L, "tiff:ImageLength", 800L));
    }

    private Teaser adapt(Object... props) {
        Resource teaser = context.create().resource(TEASER, props);
        context.currentResource(teaser);
        return context.request().adaptTo(Teaser.class);
    }

    @Test
    void readsAuthoredContent() {
        Teaser teaser = adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE,
                "id", "joey-club",
                "jcr:title", "Joey Club",
                "subtitle", " A club just for kids. ",
                "description", "<p>Activities for <b>kids</b>.</p>",
                "fileReference", ASSET,
                "alt", "Balloons",
                "imagePosition", "right",
                "linkLabel", "Find out about the Joey Club",
                "linkURL", "/content/anchora/us/en/joey-club");

        assertEquals("joey-club", teaser.getId());
        assertEquals("Joey Club", teaser.getTitle());
        assertEquals("A club just for kids.", teaser.getSubtitle());
        assertEquals("<p>Activities for <b>kids</b>.</p>", teaser.getDescription());
        assertEquals("Balloons", teaser.getImage().getAlt());
        assertEquals("right", teaser.getImagePosition());
        LinkItem link = teaser.getLink();
        assertEquals("Find out about the Joey Club", link.getLabel());
        assertEquals("/content/anchora/us/en/joey-club.html", link.getHref());
        assertNull(link.getRel());
        assertFalse(teaser.isEmpty());
    }

    @Test
    void imageIsOnTheLeftByDefault() {
        assertEquals("left", adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE, "fileReference", ASSET)
                .getImagePosition());
    }

    @Test
    void unknownImagePositionFallsBackToLeft() {
        assertEquals("left", adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE,
                "fileReference", ASSET, "imagePosition", "top").getImagePosition());
    }

    @Test
    void subtitleAloneIsNotEmpty() {
        Teaser teaser = adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE, "subtitle", "Just for kids");
        assertFalse(teaser.isEmpty());
        assertNull(teaser.getTitle());
    }

    @Test
    void headingDefaultsToH2WithoutPolicy() {
        assertEquals("h2", adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE, "jcr:title", "T")
                .getHeadingElement());
    }

    @Test
    void headingComesFromPolicy() {
        context.contentPolicyMapping(TeaserImpl.RESOURCE_TYPE, "titleType", "h3");
        assertEquals("h3", adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE, "jcr:title", "T")
                .getHeadingElement());
    }

    @Test
    void policyCannotMakeTheTeaserAnH1() {
        context.contentPolicyMapping(TeaserImpl.RESOURCE_TYPE, "titleType", "h1");
        assertEquals("h2", adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE, "jcr:title", "T")
                .getHeadingElement());
    }

    @Test
    void externalLinkInNewTabHasNoopener() {
        Teaser teaser = adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE,
                "linkLabel", "Partner", "linkURL", "https://example.com", "linkNewTab", true);

        assertEquals("https://example.com", teaser.getLink().getHref());
        assertEquals("_blank", teaser.getLink().getTarget());
        assertEquals("noopener noreferrer", teaser.getLink().getRel());
    }

    @Test
    void linkWithoutLabelIsNotRendered() {
        assertNull(adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE,
                "jcr:title", "T", "linkURL", "/content/anchora/us/en").getLink());
    }

    @Test
    void isEmptyWithoutTextOrImage() {
        Teaser teaser = adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE, "linkLabel", "Orphan link");
        assertTrue(teaser.isEmpty());
        assertNull(teaser.getId());
        assertNull(teaser.getSubtitle());
        assertNull(teaser.getImage());
    }

    @Test
    void imageAloneIsNotEmpty() {
        Teaser teaser = adapt("sling:resourceType", TeaserImpl.RESOURCE_TYPE, "fileReference", ASSET);
        assertNotNull(teaser.getImage());
        assertFalse(teaser.isEmpty());
    }
}
